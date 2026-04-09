package com.SE320.therapy.dto.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.SE320.therapy.entity.Achievement;

public class Dashboard {

    private UUID ownerUserId;
    private MonthlyTrends monthlyTrends;
    private WeeklyProgress weeklyProgress;
    private BurnoutRecovery burnoutRecovery;
    private List<Achievement> achievements;

    public Dashboard() {
        this.ownerUserId = null;
        this.monthlyTrends = new MonthlyTrends();
        this.weeklyProgress = new WeeklyProgress();
        this.burnoutRecovery = new BurnoutRecovery();
        this.achievements = new ArrayList<>();
    }

    public Dashboard(MonthlyTrends monthlyTrends,
                     WeeklyProgress weeklyProgress,
                     BurnoutRecovery burnoutRecovery,
                     List<Achievement> achievements) {
        this(null, monthlyTrends, weeklyProgress, burnoutRecovery, achievements);
    }

    public Dashboard(UUID ownerUserId,
                     MonthlyTrends monthlyTrends,
                     WeeklyProgress weeklyProgress,
                     BurnoutRecovery burnoutRecovery,
                     List<Achievement> achievements) {
        this.ownerUserId = ownerUserId;

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

        if (burnoutRecovery != null) {
            this.burnoutRecovery = burnoutRecovery;
        } else {
            this.burnoutRecovery = new BurnoutRecovery();
        }

        if (achievements != null) {
            this.achievements = new ArrayList<>(achievements);
        } else {
            this.achievements = new ArrayList<>();
        }
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public boolean isOwnedBy(UUID userId) {
        return ownerUserId != null && ownerUserId.equals(userId);
    }

    public boolean canBeViewedBy(UUID accessorUserId, UserType accessorUserType) {
        if (accessorUserType == null || ownerUserId == null) {
            return false;
        }

        return switch (accessorUserType) {
            case DOCTOR -> true;
            case PATIENT -> ownerUserId.equals(accessorUserId);
            case ADMIN -> false;
        };
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

    public BurnoutRecovery getBurnoutRecovery() {
        return burnoutRecovery;
    }

    public void setBurnoutRecovery(BurnoutRecovery burnoutRecovery) {
        if (burnoutRecovery != null) {
            this.burnoutRecovery = burnoutRecovery;
        } else {
            this.burnoutRecovery = new BurnoutRecovery();
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
