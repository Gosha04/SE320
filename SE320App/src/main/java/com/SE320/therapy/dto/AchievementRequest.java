package com.SE320.therapy.dto;

import java.time.Month;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class AchievementRequest {
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "description is required")
    private String description;
    private boolean unlocked;
    private Month unlockedMonth;

    public AchievementRequest() {
    }

    public AchievementRequest(String title, String description, boolean unlocked, Month unlockedMonth) {
        this.title = title;
        this.description = description;
        this.unlocked = unlocked;
        this.unlockedMonth = unlockedMonth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Month getUnlockedMonth() {
        return unlockedMonth;
    }

    public void setUnlockedMonth(Month unlockedMonth) {
        this.unlockedMonth = unlockedMonth;
    }

    @AssertTrue(message = "unlockedMonth is required when unlocked is true")
    public boolean isUnlockedMonthConsistent() {
        return !unlocked || unlockedMonth != null;
    }
}
