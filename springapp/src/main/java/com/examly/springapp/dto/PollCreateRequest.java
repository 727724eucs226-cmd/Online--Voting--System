package com.examly.springapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PollCreateRequest {

    private String title;
    private String description;
    private String createdBy;
    private LocalDateTime expiresAt;
    private boolean isPublic;
    private boolean allowAnonymous;
    private List<PollOptionRequest> options;

    public static class PollOptionRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }

    public List<PollOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<PollOptionRequest> options) {
        this.options = options;
    }
}