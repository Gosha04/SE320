package com.SE320.therapy.objects;

import java.time.Month;

public class Achievement {

    // Not sure what specific objects we can create here
    // Doctors should be able to create these and that should be that

    private String title;
    private String description;
    private boolean unlocked;
    private Month unlockedMonth;

    public Achievement() {
    }

    public Achievement(String title, String description, boolean unlocked, Month unlockedMonth) {
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
}
