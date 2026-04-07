package com.SE320.therapy.service;

import com.SE320.therapy.dto.AchievementRequest;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.entity.Achievement;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.objects.BurnoutRecovery;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.MaslachBurnoutInventoryDimensions;
import com.SE320.therapy.objects.MonthlyTrends;
import com.SE320.therapy.objects.ProgressPoint;
import com.SE320.therapy.objects.SessionStatus;
import com.SE320.therapy.objects.WeeklyProgress;
import com.SE320.therapy.repository.AchievementRepository;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        User user = getRequiredUser(userId);
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
        List<CBTSession> sessions = sessionRepository.findByUserId(user.getId().toString());

        MonthlyTrends monthlyTrends = buildMonthlyTrends(entries, sessions);
        WeeklyProgress weeklyProgress = buildWeeklyProgress(entries);
        BurnoutRecovery burnoutRecovery = buildBurnoutRecovery(monthlyTrends, weeklyProgress);
        syncAchievements(user, entries, sessions, weeklyProgress);
        List<Achievement> achievements = achievementRepository.findByUser_Id(userId);

        return new Dashboard(userId, monthlyTrends, weeklyProgress, burnoutRecovery, achievements);
    }

    public MonthlyTrends getMonthlyTrends(UUID userId) {
        User user = getRequiredUser(userId);
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
        List<CBTSession> sessions = sessionRepository.findByUserId(user.getId().toString());
        return buildMonthlyTrends(entries, sessions);
    }

    public WeeklyProgress getWeeklyProgress(UUID userId) {
        getRequiredUser(userId);
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
        return buildWeeklyProgress(entries);
    }

    public BurnoutRecovery getBurnoutRecovery(UUID userId) {
        User user = getRequiredUser(userId);
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
        List<CBTSession> sessions = sessionRepository.findByUserId(user.getId().toString());
        MonthlyTrends monthlyTrends = buildMonthlyTrends(entries, sessions);
        WeeklyProgress weeklyProgress = buildWeeklyProgress(entries);
        return buildBurnoutRecovery(monthlyTrends, weeklyProgress);
    }

    public List<AchievementResponse> getAchievements(UUID userId) {
        return getAchievements(userId, Pageable.unpaged()).getContent();
    }

    public Page<AchievementResponse> getAchievements(UUID userId, Pageable pageable) {
        validateUserId(userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return achievementRepository.findByUser_Id(userId, pageable)
                .map(this::toAchievementResponse);
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

    private User getRequiredUser(UUID userId) {
        validateUserId(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private BurnoutRecovery buildBurnoutRecovery(MonthlyTrends monthlyTrends, WeeklyProgress weeklyProgress) {
        double improvementRate = monthlyTrends.getImprovementRate();
        double averageMoodScore = monthlyTrends.getAverageMoodScore();
        int completedGoals = weeklyProgress.getCompletedGoals();

        double emotionalExhaustion = clampScore(70.0 - (improvementRate * 8.0) - (completedGoals * 4.0));
        double depersonalization = clampScore(55.0 - (improvementRate * 5.0) - (completedGoals * 3.0));
        double personalAccomplishment = clampScore(40.0 + (averageMoodScore * 6.0) + (completedGoals * 4.0));

        List<String> recoveryStrategies = new ArrayList<>();
        recoveryStrategies.add("Schedule one short recovery block each day for rest, reflection, or movement.");
        recoveryStrategies.add("Review mood patterns weekly to spot stress triggers early.");
        recoveryStrategies.add("Break demanding tasks into smaller steps to reduce overwhelm.");

        List<String> workLifeBalanceTechniques = new ArrayList<>();
        workLifeBalanceTechniques.add("Use a clear end-of-day shutdown routine.");
        workLifeBalanceTechniques.add("Protect at least one uninterrupted personal block outside work.");
        workLifeBalanceTechniques.add("Batch demanding work into focused windows instead of constant task switching.");

        List<String> boundarySetting = new ArrayList<>();
        boundarySetting.add("Define response-time expectations so every message does not feel urgent.");
        boundarySetting.add("Say no to nonessential commitments when energy is low.");
        boundarySetting.add("Set a hard stop for work and avoid reopening tasks afterward.");

        return new BurnoutRecovery(
                new MaslachBurnoutInventoryDimensions(
                        emotionalExhaustion,
                        depersonalization,
                        personalAccomplishment
                ),
                recoveryStrategies,
                workLifeBalanceTechniques,
                boundarySetting
        );
    }

    private double clampScore(double value) {
        if (value < 0.0) {
            return 0.0;
        }

        if (value > 100.0) {
            return 100.0;
        }

        return Math.round(value * 100.0) / 100.0;
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
