package com.SE320.therapy.dto.objects;

import java.time.YearMonth;

public class MonthlyTrends {

    private YearMonth period;
    private double averageMoodScore;
    private int sessionsCompleted;
    private int journalEntriesCreated;
    private double improvementRate;

    public MonthlyTrends() {
        this.period = YearMonth.now();
    }

    public MonthlyTrends(YearMonth period, double averageMoodScore, int sessionsCompleted,
                         int journalEntriesCreated, double improvementRate) {
        if (period != null) {
            this.period = period;
        } else {
            this.period = YearMonth.now();
        }

        this.averageMoodScore = averageMoodScore;
        this.sessionsCompleted = sessionsCompleted;
        this.journalEntriesCreated = journalEntriesCreated;
        this.improvementRate = improvementRate;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public YearMonth getCurrentMonth() {
        return getPeriod();
    }

    public void setPeriod(YearMonth period) {
        if (period != null) {
            this.period = period;
        } else {
            this.period = YearMonth.now();
        }
    }

    public double getAverageMoodScore() {
        return averageMoodScore;
    }

    public void setAverageMoodScore(double averageMoodScore) {
        this.averageMoodScore = averageMoodScore;
    }

    public int getSessionsCompleted() {
        return sessionsCompleted;
    }

    public void setSessionsCompleted(int sessionsCompleted) {
        this.sessionsCompleted = sessionsCompleted;
    }

    public int getJournalEntriesCreated() {
        return journalEntriesCreated;
    }

    public int getJournalEntriesThisMonth() {
        return getJournalEntriesCreated();
    }

    public void setJournalEntriesCreated(int journalEntriesCreated) {
        this.journalEntriesCreated = journalEntriesCreated;
    }

    public double getImprovementRate() {
        return improvementRate;
    }

    public void setImprovementRate(double improvementRate) {
        this.improvementRate = improvementRate;
    }
}
