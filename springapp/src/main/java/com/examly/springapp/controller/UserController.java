package com.examly.springapp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.dto.UpdateProfileRequest;
import com.examly.springapp.dto.UserProfileResponse;
import com.examly.springapp.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }



    // VIEW PROFILE
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication) {


        return ResponseEntity.ok(
            userService.getProfile(
                authentication.getName()
            )
        );
    }




    // UPDATE PROFILE
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {


        return ResponseEntity.ok(
            userService.updateProfile(
                authentication.getName(),
                request
            )
        );
    }

}