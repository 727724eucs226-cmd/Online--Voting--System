package com.examly.springapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.examly.springapp.model.Poll;
import com.examly.springapp.service.PollService;


@RestController
@RequestMapping("/api/admin/polls")
public class AdminPollController {


    private final PollService pollService;


    public AdminPollController(PollService pollService) {
        this.pollService = pollService;
    }


    // ADMIN VIEW ALL POLLS
    @GetMapping
    public ResponseEntity<List<Poll>> getAllPollsForAdmin() {

        return ResponseEntity.ok(
                pollService.getAllPolls()
        );
    }


    // ADMIN CLOSE ANY POLL
    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String,String>> closePoll(
            @PathVariable Long id) {


        String message =
                pollService.closePoll(id);


        return ResponseEntity.ok(
                Map.of("message", message)
        );
    }



    // ADMIN DELETE POLL
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deletePoll(
            @PathVariable Long id) {


        String message =
                pollService.deletePoll(id);


        return ResponseEntity.ok(
                Map.of("message", message)
        );
    }

}