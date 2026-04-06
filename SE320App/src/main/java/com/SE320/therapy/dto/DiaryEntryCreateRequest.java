package com.SE320.therapy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class DiaryEntryCreateRequest {

    @NotBlank(message = "situation cannot be empty")
    private String situation;

    @NotBlank(message = "automaticThought cannot be empty")
    private String automaticThought;

    @NotBlank(message = "alternativeThought cannot be empty")
    private String alternativeThought;

    @Min(value = 1, message = "moodBefore must be between 1 and 10")
    @Max(value = 10, message = "moodBefore must be between 1 and 10")
    private int moodBefore;

    @Min(value = 1, message = "moodAfter must be between 1 and 10")
    @Max(value = 10, message = "moodAfter must be between 1 and 10")
    private int moodAfter;

    public DiaryEntryCreateRequest() {
    }

    public DiaryEntryCreateRequest(String situation, String automaticThought,
                                   String alternativeThought, int moodBefore, int moodAfter) {
        this.situation = situation;
        this.automaticThought = automaticThought;
        this.alternativeThought = alternativeThought;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getAutomaticThought() {
        return automaticThought;
    }

    public void setAutomaticThought(String automaticThought) {
        this.automaticThought = automaticThought;
    }

    public String getAlternativeThought() {
        return alternativeThought;
    }

    public void setAlternativeThought(String alternativeThought) {
        this.alternativeThought = alternativeThought;
    }

    public int getMoodBefore() {
        return moodBefore;
    }

    public void setMoodBefore(int moodBefore) {
        this.moodBefore = moodBefore;
    }

    public int getMoodAfter() {
        return moodAfter;
    }

    public void setMoodAfter(int moodAfter) {
        this.moodAfter = moodAfter;
    }
}