package com.SE320.therapy.service;

import com.SE320.therapy.dto.AchievementRequest;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.entity.Achievement;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.MonthlyTrends;
import com.SE320.therapy.objects.ProgressPoint;
import com.SE320.therapy.objects.SessionStatus;
import com.SE320.therapy.objects.WeeklyProgress;
import com.SE320.therapy.repository.AchievementRepository;
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
    private final AchievementRepository achievementRepository;
    private final DiaryEntryRepository diaryEntryRepository;
    private final SessionRepository sessionRepository;

    public DashboardService(UserRepository userRepository,
                            AchievementRepository achievementRepository,
                            DiaryEntryRepository diaryEntryRepository,
                            SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
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
        syncAchievements(user, entries, sessions, weeklyProgress);
        List<Achievement> achievements = achievementRepository.findByUser_Id(userId);

        return new Dashboard(monthlyTrends, weeklyProgress, achievements);
    }

    public List<AchievementResponse> getAchievements(UUID userId) {
        validateUserId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return achievementRepository.findByUser_Id(userId).stream()
                .map(this::toAchievementResponse)
                .toList();
    }

    public AchievementResponse createAchievement(UUID userId, AchievementRequest request) {
        validateUserId(userId);
        validateAchievementRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Achievement achievement = new Achievement(
                null,
                user,
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.isUnlocked(),
                request.isUnlocked() ? request.getUnlockedMonth() : null
        );

        return toAchievementResponse(achievementRepository.save(achievement));
    }

    public AchievementResponse updateAchievement(UUID userId, UUID achievementId, AchievementRequest request) {
        validateUserId(userId);
        if (achievementId == null) {
            throw new IllegalArgumentException("Achievement ID is required.");
        }
        validateAchievementRequest(request);

        Achievement achievement = achievementRepository.findByIdAndUser_Id(achievementId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found."));

        achievement.setTitle(request.getTitle().trim());
        achievement.setDescription(request.getDescription().trim());
        achievement.setUnlocked(request.isUnlocked());
        achievement.setUnlockedMonth(request.isUnlocked() ? request.getUnlockedMonth() : null);

        return toAchievementResponse(achievementRepository.save(achievement));
    }

    public void deleteAchievement(UUID userId, UUID achievementId) {
        validateUserId(userId);
        if (achievementId == null) {
            throw new IllegalArgumentException("Achievement ID is required.");
        }

        Achievement achievement = achievementRepository.findByIdAndUser_Id(achievementId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found."));

        achievementRepository.delete(achievement);
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

    private void syncAchievements(User user,
                                  List<DiaryEntry> entries,
                                  List<CBTSession> sessions,
                                  WeeklyProgress weeklyProgress) {
        Month currentMonth = Month.from(LocalDate.now());

        upsertAchievement(
                user,
                "First Reflection",
                "Create your first diary entry.",
                !entries.isEmpty(),
                !entries.isEmpty() ? currentMonth : null
        );

        boolean hasCompletedSession = hasCompletedSessions(sessions, 1);
        upsertAchievement(
                user,
                "Session Starter",
                "Complete your first CBT session.",
                hasCompletedSession,
                hasCompletedSession ? currentMonth : null
        );

        boolean hasConsistentWeek = weeklyProgress.getCompletedGoals() >= 5;
        upsertAchievement(
                user,
                "Consistent Week",
                "Record progress on at least 5 days this week.",
                hasConsistentWeek,
                hasConsistentWeek ? currentMonth : null
        );

        boolean hasMomentum = weeklyProgress.getCurrentStreak() >= 3;
        upsertAchievement(
                user,
                "Momentum Builder",
                "Maintain a 3-day journaling streak.",
                hasMomentum,
                hasMomentum ? currentMonth : null
        );
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

    private void upsertAchievement(User user,
                                   String title,
                                   String description,
                                   boolean unlocked,
                                   Month unlockedMonth) {
        Achievement achievement = achievementRepository.findByUser_IdAndTitle(user.getId(), title)
                .orElseGet(() -> new Achievement(null, user, title, description, false, null));

        achievement.setDescription(description);
        achievement.setUnlocked(unlocked);
        achievement.setUnlockedMonth(unlocked ? unlockedMonth : null);

        achievementRepository.save(achievement);
    }

    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }
    }

    private void validateAchievementRequest(AchievementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Achievement request is required.");
        }

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Achievement title is required.");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Achievement description is required.");
        }
    }

    private AchievementResponse toAchievementResponse(Achievement achievement) {
        return new AchievementResponse(
                achievement.getId(),
                achievement.getUser().getId(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.isUnlocked(),
                achievement.getUnlockedMonth()
        );
    }
}
