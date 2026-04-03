package com.SE320.therapy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EmotionRating {

    @Column(name = "emotion", nullable = false)
    private String emotion;

    @Column(name = "rating")
    private Integer rating;

    public EmotionRating() {
    }

    public EmotionRating(String emotion, Integer rating) {
        this.emotion = emotion;
        this.rating = rating;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
