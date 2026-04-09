package com.SE320.therapy.dto.objects;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeeklyProgress {

    private DayOfWeek weekStart;
    private int completedGoals;
    private int totalGoals;
    private int currentStreak;
    private List<ProgressPoint> progressPoints;

    public WeeklyProgress() {
        this.weekStart = DayOfWeek.MONDAY;
        this.progressPoints = new ArrayList<>();
    }

    public WeeklyProgress(DayOfWeek weekStart, int completedGoals, int totalGoals,
                          int currentStreak, List<ProgressPoint> progressPoints) {
        if (weekStart != null) {
            this.weekStart = weekStart;
        } else {
            this.weekStart = DayOfWeek.MONDAY;
        }

        this.completedGoals = completedGoals;
        this.totalGoals = totalGoals;
        this.currentStreak = currentStreak;

        if (progressPoints != null) {
            this.progressPoints = new ArrayList<>(progressPoints);
        } else {
            this.progressPoints = new ArrayList<>();
        }
    }

    public DayOfWeek getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(DayOfWeek weekStart) {
        if (weekStart != null) {
            this.weekStart = weekStart;
        } else {
            this.weekStart = DayOfWeek.MONDAY;
        }
    }

    public int getCompletedGoals() {
        return completedGoals;
    }

    public void setCompletedGoals(int completedGoals) {
        this.completedGoals = completedGoals;
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(int totalGoals) {
        this.totalGoals = totalGoals;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public List<ProgressPoint> getProgressPoints() {
        return Collections.unmodifiableList(progressPoints);
    }

    public void setProgressPoints(List<ProgressPoint> progressPoints) {
        if (progressPoints != null) {
            this.progressPoints = new ArrayList<>(progressPoints);
        } else {
            this.progressPoints = new ArrayList<>();
        }
    }

    public void addProgressPoint(ProgressPoint progressPoint) {
        if (progressPoint != null) {
            progressPoints.add(progressPoint);
        }
    }
}
