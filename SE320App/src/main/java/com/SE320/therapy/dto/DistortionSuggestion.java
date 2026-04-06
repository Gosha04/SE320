package com.SE320.therapy.dto;

public class DistortionSuggestion {
    private String distortionId;
    private double confidence;
    private String reasoning;

    public DistortionSuggestion() {
    }

    public DistortionSuggestion(String distortionId, double confidence, String reasoning) {
        this.distortionId = distortionId;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }

    public String getDistortionId() {
        return distortionId;
    }

    public void setDistortionId(String distortionId) {
        this.distortionId = distortionId;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}