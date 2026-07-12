package com.examly.springapp.dto;

public class PollVoteRequest {

    private Long optionId;

    // Keep for backward compatibility with tests
    private String username;


    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}