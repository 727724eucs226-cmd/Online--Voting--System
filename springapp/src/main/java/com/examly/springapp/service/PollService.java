package com.examly.springapp.service;

import com.examly.springapp.dto.*;
import com.examly.springapp.model.*;
import com.examly.springapp.repository.*;
import com.examly.springapp.exception.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public PollService(PollRepository pollRepository, VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public Poll createPoll(PollCreateRequest req) {
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (req.getCreatedBy() == null || req.getCreatedBy().trim().isEmpty()) {
            throw new ValidationException("CreatedBy is required");
        }
        if (req.getExpiresAt() == null || !req.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ValidationException("ExpiresAt must be a future date");
        }
        if (req.getOptions() == null || req.getOptions().size() < 2) {
            throw new ValidationException("At least 2 options are required");
        }

        Poll poll = new Poll();
        poll.setTitle(req.getTitle());
        poll.setDescription(req.getDescription());
        poll.setCreatedBy(req.getCreatedBy());
        poll.setCreatedAt(LocalDateTime.now());
        poll.setExpiresAt(req.getExpiresAt());
        poll.setPublic(req.isPublic());
        poll.setAllowAnonymous(req.isAllowAnonymous());

        List<PollOption> options = new ArrayList<>();
        for (PollCreateRequest.PollOptionRequest op : req.getOptions()) {
            if (op.getText() == null || op.getText().trim().isEmpty()) {
                throw new ValidationException("Option text cannot be empty");
            }
            PollOption option = new PollOption();
            option.setText(op.getText());
            option.setVoteCount(0);
            option.setPoll(poll);
            options.add(option);
        }
        poll.setOptions(options);

        return pollRepository.save(poll);
    }

    

    @Transactional(readOnly = true)
    public List<Poll> getPublicPolls(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findByIsPublicTrue(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Poll getPoll(Long id) {
        return pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + id));
    }

    @Transactional
    public String castVote(Long pollId, PollVoteRequest req) {
        Poll poll = getPoll(pollId);

        if (poll.getExpiresAt() != null && poll.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Poll has expired");
        }

        List<Vote> existing = voteRepository.findByPollIdAndUsername(pollId, req.getUsername());
        if (!existing.isEmpty()) {
            throw new DuplicateVoteException("User has already voted in this poll");
        }

        PollOption target = null;
        for (PollOption option : poll.getOptions()) {
            if (option.getId() != null && option.getId().equals(req.getOptionId())) {
                target = option;
                break;
            }
        }
        if (target == null) {
            throw new ResourceNotFoundException("Option not found in this poll");
        }

        target.setVoteCount(target.getVoteCount() + 1);

        Vote vote = new Vote();
        vote.setPollId(pollId);
        vote.setOptionId(req.getOptionId());
        vote.setUsername(req.getUsername());
        voteRepository.save(vote);

        pollRepository.save(poll);

        return "Vote cast successfully";
    }

    @Transactional(readOnly = true)
    public PollResultsResponse getResults(Long pollId) {
        Poll poll = getPoll(pollId);
        List<Vote> votes = voteRepository.findByPollId(pollId);

        PollResultsResponse res = new PollResultsResponse();
        res.setId(poll.getId());
        res.setTotalVotes(votes.size());
        res.setOptions(new ArrayList<>(poll.getOptions()));
        return res;
    }
}