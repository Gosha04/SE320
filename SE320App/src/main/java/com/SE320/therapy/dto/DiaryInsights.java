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

    public double getAverageMoodImprovement() {
        return averageMoodImprovement;
    }

    public int getBestMoodImprovement() {
        return bestMoodImprovement;
    }
}
