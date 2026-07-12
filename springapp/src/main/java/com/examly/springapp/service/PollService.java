package com.examly.springapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examly.springapp.dto.PollCreateRequest;
import com.examly.springapp.dto.PollResultsResponse;
import com.examly.springapp.dto.PollVoteRequest;
import com.examly.springapp.exception.DuplicateVoteException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.exception.ValidationException;
import com.examly.springapp.model.Poll;
import com.examly.springapp.model.PollOption;
import com.examly.springapp.model.PollStatus;
import com.examly.springapp.model.Vote;
import com.examly.springapp.repository.PollRepository;
import com.examly.springapp.repository.VoteRepository;

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

        if (req.getExpiresAt() == null ||
            !req.getExpiresAt().isAfter(LocalDateTime.now())) {

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
        if (!req.isPublic()) {
    poll.setPrivateLink(UUID.randomUUID().toString());
} else {
    poll.setPrivateLink(null);
}

        // FR2.4 - New poll starts as OPEN
        poll.setStatus(PollStatus.OPEN);


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
    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }



    @Transactional(readOnly = true)
    public List<Poll> getPublicPolls(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return pollRepository.findByIsPublicTrue(pageable)
                .getContent();
    }



    @Transactional(readOnly = true)
    public Poll getPoll(Long id) {

        return pollRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Poll not found with id: " + id
                    ));
    }
    @Transactional(readOnly = true)
public Poll getPrivatePoll(String privateLink) {

    return pollRepository.findByPrivateLink(privateLink)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Private poll not found"));
}



   @Transactional
public String castVote(Long pollId,
                       PollVoteRequest req,
                       Authentication authentication) {

    Poll poll = getPoll(pollId);

    
   String username;

if(authentication != null) {

    username = authentication.getName();

}
else if(req.getUsername() != null 
        && !req.getUsername().isBlank()) {

    // Existing test/user flow support
    username = req.getUsername();

}
else {

    // Real anonymous voting
    if(!poll.isPublic() || !poll.isAllowAnonymous()) {

        throw new ValidationException(
            "Login required to vote"
        );
    }

    username = "ANONYMOUS_" + UUID.randomUUID();
}
    if (req.getOptionId() == null) {
        throw new ValidationException("OptionId is required");
    }

    if (poll.getStatus() == PollStatus.CLOSED) {
        throw new ValidationException("Poll is closed");
    }

    if (poll.getExpiresAt() != null &&
        poll.getExpiresAt().isBefore(LocalDateTime.now())) {

        throw new ValidationException("Poll has expired");
    }

    List<Vote> existing =
            voteRepository.findByPollIdAndUsername(
                    pollId,
                    username
            );

    if (!existing.isEmpty()) {
        throw new DuplicateVoteException(
                "User has already voted in this poll"
        );
    }

    PollOption target = null;

    for (PollOption option : poll.getOptions()) {

        if (option.getId() != null &&
            option.getId().equals(req.getOptionId())) {

            target = option;
            break;
        }
    }

    if (target == null) {
        throw new ResourceNotFoundException(
                "Option not found in this poll"
        );
    }

    target.setVoteCount(target.getVoteCount() + 1);

    Vote vote = new Vote();

    vote.setPollId(pollId);
    vote.setOptionId(req.getOptionId());
vote.setUsername(username);

    voteRepository.save(vote);
    pollRepository.save(poll);

    return "Vote cast successfully";
}

    // FR3.2 - Creator can close poll
    @Transactional
    public String closePoll(Long pollId) {

        Poll poll = getPoll(pollId);

        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new ValidationException("Poll is already closed");
        }

        poll.setStatus(PollStatus.CLOSED);

        pollRepository.save(poll);

        return "Poll closed successfully";
    }
   @Transactional
public String openPoll(Long id) {

    Poll poll = getPoll(id);

    if (poll.getStatus() == PollStatus.OPEN) {
        throw new ValidationException("Poll is already open");
    }

    poll.setStatus(PollStatus.OPEN);

    pollRepository.save(poll);

    return "Poll opened successfully";
}
@Transactional
public String changePrivacy(Long pollId, boolean isPublic, String username) {

    Poll poll = getPoll(pollId);

    // Only the creator can change privacy
    if (!poll.getCreatedBy().equals(username)) {
        throw new ValidationException(
                "Only the creator can change poll privacy");
    }

    // Privacy cannot be changed after voting starts
    if (voteRepository.existsByPollId(pollId)) {
        throw new ValidationException(
                "Privacy cannot be changed after voting has started");
    }

    poll.setPublic(isPublic);

    // Generate/remove private link
    if (isPublic) {
        poll.setPrivateLink(null);
    } else {
        if (poll.getPrivateLink() == null ||
            poll.getPrivateLink().isBlank()) {

            poll.setPrivateLink(
                java.util.UUID.randomUUID().toString()
            );
        }
    }

    pollRepository.save(poll);

    return "Poll privacy updated successfully";
}


   
   @Transactional(readOnly = true)
public PollResultsResponse getResults(Long pollId) {

    Poll poll = getPoll(pollId);

    List<Vote> votes =
            voteRepository.findByPollId(pollId);


    PollResultsResponse res =
            new PollResultsResponse();

    res.setId(poll.getId());

    res.setTotalVotes(votes.size());


    List<PollResultsResponse.OptionResult> results =
            new ArrayList<>();


    for(PollOption option : poll.getOptions()) {

        double percentage = 0;

        if(votes.size() > 0) {
            percentage =
                (option.getVoteCount() * 100.0)
                / votes.size();
        }


        results.add(
            new PollResultsResponse.OptionResult(
                option.getId(),
                option.getText(),
                option.getVoteCount(),
                percentage
            )
        );
    }


    res.setOptions(results);


    return res;
}


    @Transactional(readOnly = true)
public List<Poll> getMyPolls(String createdBy) {

    return pollRepository.findByCreatedBy(createdBy);
}
@Transactional(readOnly = true)
public List<Poll> searchPolls(String title) {

    return pollRepository.findByTitleContainingIgnoreCase(title);

}
@Transactional(readOnly = true)
public List<Poll> getPollsByStatus(PollStatus status) {

    return pollRepository.findByStatus(status);

}
@Transactional(readOnly = true)
public List<Vote> getVoteHistory(String username) {

    return voteRepository.findByUsername(username);

}
@Transactional
public String deletePoll(Long id) {


    Poll poll = getPoll(id);


    pollRepository.delete(poll);


    return "Poll deleted successfully";
}
}