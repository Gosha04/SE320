package com.SE320.therapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiaryEntrySummary {
    private UUID id;
    private String situationPreview;
    private int moodBefore;
    private int moodAfter;
    private LocalDateTime createdAt;

    public DiaryEntrySummary() {
    }

    public DiaryEntrySummary(UUID id, String situationPreview, int moodBefore,
                             int moodAfter, LocalDateTime createdAt) {
        this.id = id;
        this.situationPreview = situationPreview;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSituationPreview() {
        return situationPreview;
    }

    public void setSituationPreview(String situationPreview) {
        this.situationPreview = situationPreview;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}