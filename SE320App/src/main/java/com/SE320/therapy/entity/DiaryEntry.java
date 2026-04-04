package com.SE320.therapy.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "diary_entries")
public class DiaryEntry {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "situation", nullable = false, columnDefinition = "TEXT")
    private String situation;

    @Column(name = "automatic_thought", nullable = false, columnDefinition = "TEXT")
    private String automaticThought;

    @ElementCollection
    @CollectionTable(name = "diary_entry_emotions", joinColumns = @JoinColumn(name = "diary_entry_id"))
    private List<EmotionRating> emotions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "diary_entry_distortions",
        joinColumns = @JoinColumn(name = "diary_entry_id"),
        inverseJoinColumns = @JoinColumn(name = "distortion_id")
    )
    private List<CognitiveDistortion> distortions = new ArrayList<>();

    @Column(name = "alternative_thought", nullable = false, columnDefinition = "TEXT")
    private String alternativeThought;

    @Column(name = "mood_before", nullable = false)
    private int moodBefore;

    @Column(name = "mood_after", nullable = false)
    private int moodAfter;

    @Column(name = "belief_rating_before")
    private Integer beliefRatingBefore;

    @Column(name = "belief_rating_after")
    private Integer beliefRatingAfter;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    public DiaryEntry() {
    }

    public DiaryEntry(UUID id, User user, String situation, String automaticThought,
                      String alternativeThought, int moodBefore, int moodAfter,
                      LocalDateTime createdAt, boolean deleted) {
        this.id = id;
        this.user = user;
        this.situation = situation;
        this.automaticThought = automaticThought;
        this.alternativeThought = alternativeThought;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (deleted == null) {
            deleted = false;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<EmotionRating> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<EmotionRating> emotions) {
        this.emotions = emotions;
    }

    public List<CognitiveDistortion> getDistortions() {
        return distortions;
    }

    public void setDistortions(List<CognitiveDistortion> distortions) {
        this.distortions = distortions;
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

    public Integer getBeliefRatingBefore() {
        return beliefRatingBefore;
    }

    public void setBeliefRatingBefore(Integer beliefRatingBefore) {
        this.beliefRatingBefore = beliefRatingBefore;
    }

    public Integer getBeliefRatingAfter() {
        return beliefRatingAfter;
    }

    public void setBeliefRatingAfter(Integer beliefRatingAfter) {
        this.beliefRatingAfter = beliefRatingAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
