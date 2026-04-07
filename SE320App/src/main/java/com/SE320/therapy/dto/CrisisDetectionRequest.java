package com.SE320.therapy.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CrisisDetectionRequest {
    @Size(max = 5000, message = "message must be at most 5000 characters")
    private String message;
    private List<@NotBlank(message = "observedIndicators cannot contain blank items") String> observedIndicators;

    public CrisisDetectionRequest() {
        this.observedIndicators = new ArrayList<>();
    }

    public CrisisDetectionRequest(String message, List<String> observedIndicators) {
        this.message = message;
        setObservedIndicators(observedIndicators);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getObservedIndicators() {
        return observedIndicators;
    }

    public void setObservedIndicators(List<String> observedIndicators) {
        if (observedIndicators != null) {
            this.observedIndicators = new ArrayList<>(observedIndicators);
        } else {
            this.observedIndicators = new ArrayList<>();
        }
    }

    @AssertTrue(message = "A message or observed indicators are required.")
    public boolean hasMessageOrIndicators() {
        boolean hasMessage = message != null && !message.isBlank();
        boolean hasIndicators = observedIndicators != null && !observedIndicators.isEmpty();
        return hasMessage || hasIndicators;
    }
}
