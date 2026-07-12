package com.examly.springapp.service;

import org.springframework.stereotype.Service;

import com.examly.springapp.dto.UpdateProfileRequest;
import com.examly.springapp.dto.UserProfileResponse;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;


@Service
public class UserService {


    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public UserProfileResponse getProfile(String username) {


        User user =
            userRepository.findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException("User not found")
            );


        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );
    }




    public UserProfileResponse updateProfile(
            String currentUsername,
            UpdateProfileRequest request) {


        User user =
            userRepository.findByUsername(currentUsername)
            .orElseThrow(() ->
                new RuntimeException("User not found")
            );


        user.setUsername(request.getUsername());


        User updated =
                userRepository.save(user);


        return new UserProfileResponse(
                updated.getId(),
                updated.getUsername(),
                updated.getRole().name()
        );
    }

}