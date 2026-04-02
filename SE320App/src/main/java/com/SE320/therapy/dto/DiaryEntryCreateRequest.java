package com.SE320.therapy.dto;

public class DiaryEntryCreateRequest {
    private String situation;
    private String automaticThought;
    private String alternativeThought;
    private int moodBefore;
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