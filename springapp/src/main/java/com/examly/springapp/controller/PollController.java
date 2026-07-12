package com.examly.springapp.controller;
//this is
import com.examly.springapp.dto.*;
import com.examly.springapp.model.Poll;
import com.examly.springapp.service.PollService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    // GET POLL DETAILS
    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPoll(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getPoll(id));
    }

    // VOTE
    @PostMapping("/{id}/vote")
    public ResponseEntity<Map<String, String>> vote(
            @PathVariable Long id,
            @RequestBody PollVoteRequest req) {

        String msg = pollService.castVote(id, req);
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // RESULTS
    @GetMapping("/{id}/results")
    public ResponseEntity<PollResultsResponse> results(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getResults(id));
    }
}