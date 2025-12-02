package com.example.mentalhealth.model;

public class UserProgress {

    private String progressMessage;

    // Constructor and Getter
    public UserProgress(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }
}

