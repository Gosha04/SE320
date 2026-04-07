package com.SE320.therapy.dto;

import java.util.ArrayList;
import java.util.List;

public class CrisisDetectionRequest {
    private String message;
    private List<String> observedIndicators;

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
}
