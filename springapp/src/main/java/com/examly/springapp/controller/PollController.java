package com.examly.springapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.dto.PollCreateRequest;
import com.examly.springapp.dto.PollResultsResponse;
import com.examly.springapp.dto.PollVoteRequest;
import com.examly.springapp.model.Poll;
import com.examly.springapp.model.PollStatus;
import com.examly.springapp.model.Vote;
import com.examly.springapp.service.PollService;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    // CREATE POLL
    @PostMapping
    public ResponseEntity<Poll> createPoll(@RequestBody PollCreateRequest req) {
        return new ResponseEntity<>(pollService.createPoll(req), HttpStatus.CREATED);
    }

    // PUBLIC POLLS
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicPolls(
            @RequestParam int page,
            @RequestParam int size) {

        List<Poll> polls = pollService.getPublicPolls(page, size);

        Map<String, Object> res = new HashMap<>();
        res.put("content", polls);

        return ResponseEntity.ok(res);
    }

    // ALL POLLS
    @GetMapping
    public ResponseEntity<List<Poll>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    // MY POLLS (Creator Dashboard)
    @GetMapping("/my")
    public ResponseEntity<List<Poll>> getMyPolls(Authentication authentication) {

        return ResponseEntity.ok(
                pollService.getMyPolls(authentication.getName())
        );
    }
@GetMapping("/history")
public ResponseEntity<List<Vote>> getVoteHistory(
        Authentication authentication) {

    return ResponseEntity.ok(
            pollService.getVoteHistory(authentication.getName())
    );
}
// SEARCH POLLS
@GetMapping("/search")
public ResponseEntity<List<Poll>> searchPolls(
        @RequestParam String title) {

    return ResponseEntity.ok(
            pollService.searchPolls(title)
    );
}
// FILTER POLLS BY STATUS
@GetMapping("/status")
public ResponseEntity<List<Poll>> getPollsByStatus(
        @RequestParam PollStatus status) {

    return ResponseEntity.ok(
            pollService.getPollsByStatus(status)
    );
}
    // GET POLL DETAILS
    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPoll(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getPoll(id));
    }
@GetMapping("/private/{privateLink}")
public ResponseEntity<Poll> getPrivatePoll(
        @PathVariable String privateLink) {

    return ResponseEntity.ok(
            pollService.getPrivatePoll(privateLink)
    );
}
    // VOTE
   @PostMapping("/{id}/vote")
public ResponseEntity<Map<String, String>> vote(
        @PathVariable Long id,
        @RequestBody PollVoteRequest req,
        Authentication authentication) {

    String msg = pollService.castVote(id, req, authentication);

    return ResponseEntity.ok(
            Map.of("message", msg)
    );
}

    // VOTE PAGE
    @GetMapping("/{id}/vote")
    public ResponseEntity<Map<String, String>> votePage(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Vote cast successfully");

        return ResponseEntity.ok(response);
    }

    // RESULTS
    @GetMapping("/{id}/results")
    public ResponseEntity<PollResultsResponse> results(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pollService.getResults(id)
        );
    }
@PutMapping("/{id}/privacy")
public ResponseEntity<Map<String, String>> changePrivacy(
        @PathVariable Long id,
        @RequestParam boolean isPublic,
        Authentication authentication) {

    String message = pollService.changePrivacy(
            id,
            isPublic,
            authentication.getName()
    );

    return ResponseEntity.ok(
            Map.of("message", message)
    );
}
    // CLOSE POLL
    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String, String>> closePoll(
            @PathVariable Long id) {

        pollService.closePoll(id);

        return ResponseEntity.ok(
                Map.of("message", "Poll closed successfully")
        );
    }
    // OPEN POLL
@PutMapping("/{id}/open")
public ResponseEntity<Map<String, String>> openPoll(
        @PathVariable Long id) {

    String message = pollService.openPoll(id);

return ResponseEntity.ok(
        Map.of("message", message)
);
}
}