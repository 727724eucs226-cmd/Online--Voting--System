package com.examly.springapp.dto;

import com.examly.springapp.model.PollOption;
import java.util.List;

public class PollResultsResponse {

    private Long id;
    private int totalVotes;
    private List<PollOption> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }
}