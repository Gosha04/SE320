package com.SE320.therapy.service;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.objects.Achievement;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.MonthlyTrends;
import com.SE320.therapy.objects.ProgressPoint;
import com.SE320.therapy.objects.SessionStatus;
import com.SE320.therapy.objects.WeeklyProgress;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final DiaryEntryRepository diaryEntryRepository;
    private final SessionRepository sessionRepository;

    public DashboardService(UserRepository userRepository,
                            DiaryEntryRepository diaryEntryRepository,
                            SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.diaryEntryRepository = diaryEntryRepository;
        this.sessionRepository = sessionRepository;
    }

    public Dashboard getDashboard(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
        List<CBTSession> sessions = sessionRepository.findByUserId(user.getId().toString());

        MonthlyTrends monthlyTrends = buildMonthlyTrends(entries, sessions);
        WeeklyProgress weeklyProgress = buildWeeklyProgress(entries);
        List<Achievement> achievements = buildAchievements(entries, sessions, weeklyProgress);

        return new Dashboard(monthlyTrends, weeklyProgress, achievements);
    }

    private MonthlyTrends buildMonthlyTrends(List<DiaryEntry> entries, List<CBTSession> sessions) {
        YearMonth currentMonth = YearMonth.now();
        List<DiaryEntry> monthlyEntries = new ArrayList<>();
        int sessionsCompleted = 0;

        for (DiaryEntry entry : entries) {
            if (entry.getCreatedAt() != null && YearMonth.from(entry.getCreatedAt()).equals(currentMonth)) {
                monthlyEntries.add(entry);
            }
        }

        for (CBTSession session : sessions) {
            if (session.getStatus() == SessionStatus.ENDED
                    && session.getEndedAt() != null
                    && YearMonth.from(session.getEndedAt()).equals(currentMonth)) {
                sessionsCompleted++;
            }
        }

        double averageMoodScore = 0.0;
        double improvementRate = 0.0;

        if (!monthlyEntries.isEmpty()) {
            int totalMoodAfter = 0;
            int totalImprovement = 0;

            for (DiaryEntry entry : monthlyEntries) {
                totalMoodAfter += entry.getMoodAfter();
                totalImprovement += entry.getMoodAfter() - entry.getMoodBefore();
            }

            averageMoodScore = (double) totalMoodAfter / monthlyEntries.size();
            improvementRate = (double) totalImprovement / monthlyEntries.size();
        }

        return new MonthlyTrends(
                currentMonth,
                averageMoodScore,
                sessionsCompleted,
                monthlyEntries.size(),
                improvementRate
        );
    }

    private WeeklyProgress buildWeeklyProgress(List<DiaryEntry> entries) {
        LocalDate today = LocalDate.now();
        LocalDate weekStartDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDate = weekStartDate.plusDays(6);

        int completedGoals = 0;
        int totalGoals = 7;
        List<ProgressPoint> progressPoints = new ArrayList<>();

        for (int i = 0; i < totalGoals; i++) {
            LocalDate currentDate = weekStartDate.plusDays(i);
            int dailyCount = 0;

            for (DiaryEntry entry : entries) {
                LocalDateTime createdAt = entry.getCreatedAt();
                if (createdAt != null && createdAt.toLocalDate().isEqual(currentDate)) {
                    dailyCount++;
                }
            }

            if (dailyCount > 0) {
                completedGoals++;
            }

            progressPoints.add(new ProgressPoint(
                    currentDate.getDayOfWeek().name().substring(0, 3),
                    dailyCount
            ));
        }

        int currentStreak = calculateCurrentStreak(entries, weekEndDate);

        return new WeeklyProgress(
                DayOfWeek.MONDAY,
                completedGoals,
                totalGoals,
                currentStreak,
                progressPoints
        );
    }

    private int calculateCurrentStreak(List<DiaryEntry> entries, LocalDate endDate) {
        List<LocalDate> entryDates = entries.stream()
                .map(DiaryEntry::getCreatedAt)
                .filter(createdAt -> createdAt != null)
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        int streak = 0;
        LocalDate expectedDate = endDate;

        for (LocalDate entryDate : entryDates) {
            if (entryDate.isAfter(expectedDate)) {
                continue;
            }

            if (entryDate.isEqual(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (streak == 0) {
                expectedDate = entryDate;
                streak = 1;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    private List<Achievement> buildAchievements(List<DiaryEntry> entries,
                                                List<CBTSession> sessions,
                                                WeeklyProgress weeklyProgress) {
        List<Achievement> achievements = new ArrayList<>();
        Month currentMonth = Month.from(LocalDate.now());

        achievements.add(new Achievement(
                "First Reflection",
                "Create your first diary entry.",
                !entries.isEmpty(),
                !entries.isEmpty() ? currentMonth : null
        ));

        achievements.add(new Achievement(
                "Session Starter",
                "Complete your first CBT session.",
                hasCompletedSessions(sessions, 1),
                hasCompletedSessions(sessions, 1) ? currentMonth : null
        ));

        achievements.add(new Achievement(
                "Consistent Week",
                "Record progress on at least 5 days this week.",
                weeklyProgress.getCompletedGoals() >= 5,
                weeklyProgress.getCompletedGoals() >= 5 ? currentMonth : null
        ));

        achievements.add(new Achievement(
                "Momentum Builder",
                "Maintain a 3-day journaling streak.",
                weeklyProgress.getCurrentStreak() >= 3,
                weeklyProgress.getCurrentStreak() >= 3 ? currentMonth : null
        ));

        return achievements;
    }

    private boolean hasCompletedSessions(List<CBTSession> sessions, int minimumCompletedSessions) {
        int completedSessions = 0;

        for (CBTSession session : sessions) {
            if (session.getStatus() == SessionStatus.ENDED) {
                completedSessions++;
            }
        }

        return completedSessions >= minimumCompletedSessions;
    }
}
