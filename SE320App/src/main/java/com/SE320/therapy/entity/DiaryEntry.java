package com.SE320.therapy.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class DiaryEntry {
    private UUID id;
    private UUID userId;
    private String situation;
    private String automaticThought;
    private String alternativeThought;
    private int moodBefore;
    private int moodAfter;
    private LocalDateTime createdAt;
    private boolean deleted;

    public DiaryEntry() {
    }

    public DiaryEntry(UUID id, UUID userId, String situation, String automaticThought,
                      String alternativeThought, int moodBefore, int moodAfter,
                      LocalDateTime createdAt, boolean deleted) {
        this.id = id;
        this.userId = userId;
        this.situation = situation;
        this.automaticThought = automaticThought;
        this.alternativeThought = alternativeThought;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }
}
