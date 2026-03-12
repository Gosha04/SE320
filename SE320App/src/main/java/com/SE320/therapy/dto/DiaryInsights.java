package com.SE320.therapy.dto;

public class DiaryInsights {
    private int totalEntries;
    private double averageMoodImprovement;
    private int bestMoodImprovement;

    public DiaryInsights() {
    }

    public DiaryInsights(int totalEntries, double averageMoodImprovement, int bestMoodImprovement) {
        this.totalEntries = totalEntries;
        this.averageMoodImprovement = averageMoodImprovement;
        this.bestMoodImprovement = bestMoodImprovement;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }

    public double getAverageMoodImprovement() {
        return averageMoodImprovement;
    }

    public void setAverageMoodImprovement(double averageMoodImprovement) {
        this.averageMoodImprovement = averageMoodImprovement;
    }

    public int getBestMoodImprovement() {
        return bestMoodImprovement;
    }

    public void setBestMoodImprovement(int bestMoodImprovement) {
        this.bestMoodImprovement = bestMoodImprovement;
    }
}