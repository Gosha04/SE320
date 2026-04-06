package com.SE320.therapy.dto;

import jakarta.validation.constraints.NotBlank;

public class DistortionSuggestionRequest {

    @NotBlank(message = "thought must not be blank")
    private String thought;

    public DistortionSuggestionRequest() {
    }

    public DistortionSuggestionRequest(String thought) {
        this.thought = thought;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }
}