package com.examly.springapp.dto;

import java.util.List;

public class PollResultsResponse {

    private Long id;

    private int totalVotes;

    private List<OptionResult> options;


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


    public List<OptionResult> getOptions() {
        return options;
    }


    public void setOptions(List<OptionResult> options) {
        this.options = options;
    }



    public static class OptionResult {

        private Long id;

        private String text;

        private int voteCount;

        private double percentage;


        public OptionResult(
                Long id,
                String text,
                int voteCount,
                double percentage) {

            this.id = id;
            this.text = text;
            this.voteCount = voteCount;
            this.percentage = percentage;
        }


        public Long getId() {
            return id;
        }


        public String getText() {
            return text;
        }


        public int getVoteCount() {
            return voteCount;
        }


        public double getPercentage() {
            return percentage;
        }
    }
}