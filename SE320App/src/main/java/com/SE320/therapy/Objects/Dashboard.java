package com.SE320.therapy.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dashboard {

    private MonthlyTrends monthlyTrends;
    private WeeklyProgress weeklyProgress;
    private List<Achievement> achievements;

    public Dashboard() {
        this.monthlyTrends = new MonthlyTrends();
        this.weeklyProgress = new WeeklyProgress();
        this.achievements = new ArrayList<>();
    }

    public Dashboard(MonthlyTrends monthlyTrends, WeeklyProgress weeklyProgress, List<Achievement> achievements) {
        if (monthlyTrends != null) {
            this.monthlyTrends = monthlyTrends;
        } else {
            this.monthlyTrends = new MonthlyTrends();
        }

        if (weeklyProgress != null) {
            this.weeklyProgress = weeklyProgress;
        } else {
            this.weeklyProgress = new WeeklyProgress();
        }

        if (achievements != null) {
            this.achievements = new ArrayList<>(achievements);
        } else {
            this.achievements = new ArrayList<>();
        }
    }

    public MonthlyTrends getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(MonthlyTrends monthlyTrends) {
        if (monthlyTrends != null) {
            this.monthlyTrends = monthlyTrends;
        } else {
            this.monthlyTrends = new MonthlyTrends();
        }
    }

    public WeeklyProgress getWeeklyProgress() {
        return weeklyProgress;
    }

    public void setWeeklyProgress(WeeklyProgress weeklyProgress) {
        if (weeklyProgress != null) {
            this.weeklyProgress = weeklyProgress;
        } else {
            this.weeklyProgress = new WeeklyProgress();
        }
    }

    public List<Achievement> getAchievements() {
        return Collections.unmodifiableList(achievements);
    }

    public void setAchievements(List<Achievement> achievements) {
        if (achievements != null) {
            this.achievements = new ArrayList<>(achievements);
        } else {
            this.achievements = new ArrayList<>();
        }
    }

    public void addAchievement(Achievement achievement) {
        if (achievement != null) {
            achievements.add(achievement);
        }
    }
}
