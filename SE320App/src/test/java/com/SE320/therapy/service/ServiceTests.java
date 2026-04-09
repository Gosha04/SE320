package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.dto.AchievementRequest;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.dto.CrisisDetectionRequest;
import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.RegisterRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.objects.InteractionModality;
import com.SE320.therapy.dto.objects.SeverityLevel;
import com.SE320.therapy.dto.objects.SessionStatus;
import com.SE320.therapy.dto.objects.UserSessionStatus;
import com.SE320.therapy.dto.objects.UserType;
import com.SE320.therapy.entity.Achievement;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.SafetyPlan;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.exception.ApiException;
import com.SE320.therapy.repository.AchievementRepository;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.SafetyPlanRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.repository.UserSessionRepository;
import com.SE320.therapy.security.JwtService;

@ExtendWith(MockitoExtension.class)
class ServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private DiaryEntryRepository diaryEntryRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SafetyPlanRepository safetyPlanRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private AiService aiService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private DashboardService dashboardService;
    private CrisisService crisisService;
    private SessionService sessionService;
    private DiaryServiceImpl diaryService;
    private DiaryServiceImpl diaryServiceWithoutAi;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(userRepository, achievementRepository, diaryEntryRepository, sessionRepository);
        crisisService = new CrisisService(userRepository, safetyPlanRepository);
        sessionService = new SessionService(sessionRepository, userRepository, userSessionRepository, chatMessageRepository, aiService);
        diaryService = new DiaryServiceImpl(diaryEntryRepository, userRepository, aiService);
        diaryServiceWithoutAi = new DiaryServiceImpl(diaryEntryRepository, userRepository);
    }

    @Test
    void dashboardComputesMonthlyTrendsAndSyncsAchievements() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);

        DiaryEntry currentMonthEntryOne = diaryEntry(user, 4, 7, LocalDateTime.now().minusDays(1));
        DiaryEntry currentMonthEntryTwo = diaryEntry(user, 5, 6, LocalDateTime.now());
        DiaryEntry previousMonthEntry = diaryEntry(user, 3, 4, LocalDateTime.now().minusMonths(1));

        CBTSession completedCurrentMonth = userSessionRecord(userId.toString(), SessionStatus.ENDED, LocalDateTime.now());
        CBTSession activeCurrentMonth = userSessionRecord(userId.toString(), SessionStatus.ACTIVE, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId))
            .thenReturn(List.of(currentMonthEntryOne, currentMonthEntryTwo, previousMonthEntry));
        when(sessionRepository.findByUserId(userId.toString())).thenReturn(List.of(completedCurrentMonth, activeCurrentMonth));
        when(achievementRepository.findByUser_IdAndTitle(userId, "First Reflection")).thenReturn(Optional.empty());
        when(achievementRepository.findByUser_IdAndTitle(userId, "Session Starter")).thenReturn(Optional.empty());
        when(achievementRepository.findByUser_IdAndTitle(userId, "Consistent Week")).thenReturn(Optional.empty());
        when(achievementRepository.findByUser_IdAndTitle(userId, "Momentum Builder")).thenReturn(Optional.empty());
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(achievementRepository.findByUser_Id(userId)).thenReturn(List.of(
            new Achievement(UUID.randomUUID(), user, "First Reflection", "Create your first diary entry.", true, Month.from(LocalDateTime.now()))
        ));

        var dashboard = dashboardService.getDashboard(userId);

        assertEquals(YearMonth.now(), dashboard.getMonthlyTrends().getCurrentMonth());
        assertEquals(2, dashboard.getMonthlyTrends().getJournalEntriesThisMonth());
        assertEquals(1, dashboard.getMonthlyTrends().getSessionsCompleted());
        assertEquals(6.5d, dashboard.getMonthlyTrends().getAverageMoodScore());
        assertEquals(2.0d, dashboard.getMonthlyTrends().getImprovementRate());
        assertEquals(1, dashboard.getAchievements().size());
        verify(achievementRepository, times(4)).save(any(Achievement.class));
    }

    @Test
    void getMonthlyTrendsReturnsComputedMonthlyMetrics() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);

        DiaryEntry currentMonthEntryOne = diaryEntry(user, 4, 7, LocalDateTime.now().minusDays(1));
        DiaryEntry currentMonthEntryTwo = diaryEntry(user, 5, 6, LocalDateTime.now());
        DiaryEntry previousMonthEntry = diaryEntry(user, 3, 4, LocalDateTime.now().minusMonths(1));

        CBTSession completedCurrentMonth = userSessionRecord(userId.toString(), SessionStatus.ENDED, LocalDateTime.now());
        CBTSession activeCurrentMonth = userSessionRecord(userId.toString(), SessionStatus.ACTIVE, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId))
            .thenReturn(List.of(currentMonthEntryOne, currentMonthEntryTwo, previousMonthEntry));
        when(sessionRepository.findByUserId(userId.toString())).thenReturn(List.of(completedCurrentMonth, activeCurrentMonth));

        var monthlyTrends = dashboardService.getMonthlyTrends(userId);

        assertEquals(YearMonth.now(), monthlyTrends.getCurrentMonth());
        assertEquals(2, monthlyTrends.getJournalEntriesThisMonth());
        assertEquals(1, monthlyTrends.getSessionsCompleted());
        assertEquals(6.5d, monthlyTrends.getAverageMoodScore());
        assertEquals(2.0d, monthlyTrends.getImprovementRate());
    }

    @Test
    void getWeeklyProgressBuildsWeekPointsAndStreak() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        LocalDateTime today = LocalDateTime.now();

        DiaryEntry todayEntry = diaryEntry(user, 2, 6, today);
        DiaryEntry previousDayEntry = diaryEntry(user, 3, 7, today.minusDays(1));
        DiaryEntry oldEntry = diaryEntry(user, 4, 5, today.minusDays(10));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId))
            .thenReturn(List.of(todayEntry, previousDayEntry, oldEntry));

        var weeklyProgress = dashboardService.getWeeklyProgress(userId);

        assertEquals(7, weeklyProgress.getTotalGoals());
        assertEquals(2, weeklyProgress.getCompletedGoals());
        assertEquals(2, weeklyProgress.getCurrentStreak());
        assertEquals(7, weeklyProgress.getProgressPoints().size());
        assertEquals(2.0d, weeklyProgress.getProgressPoints().stream().mapToDouble(point -> point.getValue()).sum());
    }

    @Test
    void getBurnoutRecoveryBuildsRecoveryDimensionsFromMonthlyAndWeeklySignals() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);

        DiaryEntry entry = diaryEntry(user, 4, 8, LocalDateTime.now());
        CBTSession completedCurrentMonth = userSessionRecord(userId.toString(), SessionStatus.ENDED, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId)).thenReturn(List.of(entry));
        when(sessionRepository.findByUserId(userId.toString())).thenReturn(List.of(completedCurrentMonth));

        var burnoutRecovery = dashboardService.getBurnoutRecovery(userId);

        assertEquals(34.0d, burnoutRecovery.getMaslachBurnoutInventoryDimensions().getEmotionalExhaustion());
        assertEquals(32.0d, burnoutRecovery.getMaslachBurnoutInventoryDimensions().getDepersonalization());
        assertEquals(92.0d, burnoutRecovery.getMaslachBurnoutInventoryDimensions().getPersonalAccomplishment());
        assertEquals(3, burnoutRecovery.getRecoveryStrategies().size());
        assertEquals(3, burnoutRecovery.getWorkLifeBalanceTechniques().size());
        assertEquals(3, burnoutRecovery.getBoundarySetting().size());
    }

    @Test
    void getAchievementsReturnsUnpagedMappedContent() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        Achievement achievement = new Achievement(
            UUID.randomUUID(),
            user,
            "First Reflection",
            "Create your first diary entry.",
            true,
            Month.from(LocalDateTime.now())
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(achievementRepository.findByUser_Id(userId, Pageable.unpaged()))
            .thenReturn(new PageImpl<>(List.of(achievement)));

        List<AchievementResponse> achievements = dashboardService.getAchievements(userId);

        assertEquals(1, achievements.size());
        verify(achievementRepository).findByUser_Id(userId, Pageable.unpaged());
    }

    @Test
    void getAchievementsWithPageableThrowsWhenUserIsMissing() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> dashboardService.getAchievements(userId, PageRequest.of(0, 10))
        );

        assertEquals("User not found.", exception.getMessage());
        verify(achievementRepository, never()).findByUser_Id(any(UUID.class), any(Pageable.class));
    }

    @Test
    void getAchievementsWithPageableThrowsWhenUserIdIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> dashboardService.getAchievements(null, PageRequest.of(0, 10))
        );

        assertEquals("User ID is required.", exception.getMessage());
        verify(userRepository, never()).findById(any(UUID.class));
        verify(achievementRepository, never()).findByUser_Id(any(UUID.class), any(Pageable.class));
    }

    @Test
    void createAchievementTrimsInputAndClearsUnlockedMonthWhenLocked() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> {
            Achievement achievement = invocation.getArgument(0);
            return new Achievement(
                UUID.randomUUID(),
                achievement.getUser(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.isUnlocked(),
                achievement.getUnlockedMonth()
            );
        });

        var response = dashboardService.createAchievement(
            userId,
            new AchievementRequest("  Keep Going  ", "  Stay consistent  ", false, Month.JANUARY)
        );

        assertEquals("Keep Going", response.title());
        assertEquals("Stay consistent", response.description());
        assertTrue(!response.unlocked());
        assertNull(response.unlockedMonth());
    }

    @Test
    void crisisDetectionFlagsSignificantSeverityOnSelfHarmLanguage() {
        CrisisDetectionRequest request = new CrisisDetectionRequest(
            "I feel hopeless and I might kill myself.",
            List.of("withdrawal")
        );

        var response = crisisService.detectCrisisIndicators(request);

        assertTrue(response.crisisDetected());
        assertEquals(SeverityLevel.SIGNIFICANT, response.severityLevel());
        assertTrue(response.matchedIndicators().stream().anyMatch(i -> i.toLowerCase().contains("suicide")));
        assertTrue(response.recommendedNextSteps().stream().anyMatch(step -> step.contains("988")));
    }

    @Test
    void saveSafetyPlanPersistsCustomStepsForUser() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        List<String> steps = List.of("Call roommate", "Take medication", "Contact counselor");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(safetyPlanRepository.findByUser_Id(userId)).thenReturn(Optional.empty());
        when(safetyPlanRepository.save(any(SafetyPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<String> saved = crisisService.saveSafetyPlan(userId, steps);

        assertEquals(steps, saved);
        ArgumentCaptor<SafetyPlan> captor = ArgumentCaptor.forClass(SafetyPlan.class);
        verify(safetyPlanRepository).save(captor.capture());
        assertEquals(userId, captor.getValue().getUser().getId());
        assertEquals(steps, captor.getValue().getSteps());
    }

    @Test
    void getCrisisForUserFallsBackToDefaultSafetyPlanWhenNoSavedPlan() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(safetyPlanRepository.findByUser_Id(userId)).thenReturn(Optional.empty());

        var crisis = crisisService.getCrisis(userId);

        assertEquals(userId, crisis.getOwnerUserId());
        assertTrue(crisis.getSafetyPlanningSteps().size() >= 3);
    }

    @Test
    void sendChatMessageDefaultsToTextModalityWhenRequestModalityIsNull() {
        UUID userId = UUID.randomUUID();
        CBTSession session = librarySession(1001L, "Thought Record");
        User user = user(userId);
        UserSession active = activeUserSession(user, session);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
            userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(active));
        when(aiService.generateResponse(active.getId(), "I keep overthinking"))
            .thenReturn("Let's unpack the thought: I keep overthinking");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage message = invocation.getArgument(0);
            message.setId(UUID.randomUUID());
            message.setTimestamp(LocalDateTime.now());
            return message;
        });

        var response = sessionService.sendChatMessage(
            1001L,
            new SendChatMessageRequest(userId, "I keep overthinking", null)
        );

        assertEquals("TEXT", response.userMessage().modality());
        assertEquals("TEXT", response.assistantMessage().modality());
        assertTrue(response.assistantMessage().content().contains("I keep overthinking"));
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
    }

    @Test
    void createDiaryEntryTrimsFieldsAndPersists() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        DiaryEntryCreateRequest request = new DiaryEntryCreateRequest(
            "  Situation text  ",
            "  Automatic thought  ",
            "  Alternative thought  ",
            3,
            7
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryEntryRepository.save(any(DiaryEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = diaryService.createEntry(userId, request);

        assertNotNull(response.id());
        assertEquals("Diary entry created successfully.", response.message());

        ArgumentCaptor<DiaryEntry> captor = ArgumentCaptor.forClass(DiaryEntry.class);
        verify(diaryEntryRepository).save(captor.capture());
        assertEquals("Situation text", captor.getValue().getSituation());
        assertEquals("Automatic thought", captor.getValue().getAutomaticThought());
        assertEquals("Alternative thought", captor.getValue().getAlternativeThought());
        assertEquals(3, captor.getValue().getMoodBefore());
        assertEquals(7, captor.getValue().getMoodAfter());
    }

    @Test
    void getDiaryEntriesReturnsPagedSummariesWithTruncatedPreview() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        DiaryEntry longSituationEntry = diaryEntry(user, 4, 6, LocalDateTime.now().minusHours(2));
        DiaryEntry shortSituationEntry = diaryEntry(user, 5, 7, LocalDateTime.now().minusHours(1));
        longSituationEntry.setSituation("This is a very long situation text that should be truncated for preview usage.");
        shortSituationEntry.setSituation("Short preview");

        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId))
            .thenReturn(List.of(longSituationEntry, shortSituationEntry));

        var page = diaryService.getEntries(userId, PageRequest.of(0, 1));

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("This is a very long situation text that ...", page.getContent().get(0).getSituationPreview());
    }

    @Test
    void getDiaryEntryDetailThrowsNotFoundWhenEntryDoesNotExist() {
        UUID entryId = UUID.randomUUID();
        when(diaryEntryRepository.findByIdAndDeletedFalse(entryId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> diaryService.getEntryDetail(entryId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("DIARY_ENTRY_NOT_FOUND", exception.getCode());
    }

    @Test
    void deleteDiaryEntryMarksEntryAsDeleted() {
        UUID entryId = UUID.randomUUID();
        User user = user(UUID.randomUUID());
        DiaryEntry entry = diaryEntry(user, 2, 6, LocalDateTime.now());
        entry.setDeleted(false);

        when(diaryEntryRepository.findByIdAndDeletedFalse(entryId)).thenReturn(Optional.of(entry));
        when(diaryEntryRepository.save(any(DiaryEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        diaryService.deleteEntry(entryId);

        assertTrue(entry.getDeleted());
        verify(diaryEntryRepository).save(entry);
    }

    @Test
    void getInsightsComputesFallbackMetricsWhenAiUnavailable() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        DiaryEntry entryOne = diaryEntry(user, 3, 6, LocalDateTime.now().minusDays(2));
        DiaryEntry entryTwo = diaryEntry(user, 6, 7, LocalDateTime.now().minusDays(1));

        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId)).thenReturn(List.of(entryOne, entryTwo));

        var insights = diaryServiceWithoutAi.getInsights(userId);

        assertEquals(2, insights.getTotalEntries());
        assertEquals(2.0d, insights.getAverageMoodImprovement());
        assertEquals(3, insights.getBestMoodImprovement());
    }

    @Test
    void suggestDistortionsUsesRuleBasedFallbackWhenAiUnavailable() {
        var suggestions = diaryServiceWithoutAi.suggestDistortions("I always fail and this is a disaster");

        assertEquals(2, suggestions.size());
        assertEquals("all-or-nothing", suggestions.get(0).getDistortionId());
        assertEquals("catastrophizing", suggestions.get(1).getDistortionId());
    }

    @Test
    void suggestDistortionsRejectsBlankThought() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> diaryService.suggestDistortions("   ")
        );

        assertEquals("Thought cannot be empty.", exception.getMessage());
    }

    @Test
    void endSessionAddsAiSummaryMessageWhenProvided() {
        UUID userId = UUID.randomUUID();
        CBTSession session = librarySession(1001L, "Thought Record");
        User user = user(userId);
        UserSession active = activeUserSession(user, session);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
            userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(active));
        when(aiService.summarizeSession(active.getId())).thenReturn("  Session recap  ");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = sessionService.endSession(1001L, new EndSessionRequest(userId, 8));

        assertEquals("COMPLETED", response.status());
        assertEquals(8, response.moodAfter());

        ArgumentCaptor<ChatMessage> chatCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(chatCaptor.capture());
        assertEquals("Session recap", chatCaptor.getValue().getContent());
        assertEquals(InteractionModality.TEXT, chatCaptor.getValue().getModality());
    }

    @Test
    void refreshTokenRejectsTokenReuse() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        UUID userId = UUID.randomUUID();
        User user = user(userId);

        JwtService.TokenClaims claims = new JwtService.TokenClaims(
            userId,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType(),
            "refresh",
            Instant.now().plusSeconds(300),
            new AuthenticatedUser(userId, user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserType())
        );

        when(jwtService.parseRefreshToken("refresh-token")).thenReturn(claims);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh");

        AuthResponse first = authentication.refreshToken("refresh-token");

        assertNotNull(first);
        assertEquals("new-access", first.accessToken());

        ResponseStatusException second = assertThrows(
            ResponseStatusException.class,
            () -> authentication.refreshToken("refresh-token")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, second.getStatusCode());
    }

    @Test
    void registerCreatesUserIssuesTokensAndMarksUserOnline() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        RegisterRequest request = new RegisterRequest(
            UserType.PATIENT,
            "Alex",
            "Doe",
            "alex@example.com",
            "password123",
            "5551112222"
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        AuthResponse response = authentication.register(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("alex@example.com", response.user().email());
        assertTrue(response.user().online());
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        RegisterRequest request = new RegisterRequest(
            UserType.PATIENT,
            "Alex",
            "Doe",
            "alex@example.com",
            "password123",
            "5551112222"
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user(UUID.randomUUID())));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authentication.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserDeletesRequesterWhenCredentialsAreValid() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        UUID userId = UUID.randomUUID();
        User requester = user(userId);
        String email = "self@example.com";
        requester.setEmail(email);
        requester.setPasswordHash("stored-hash");

        when(userRepository.findPasswordByEmail(email)).thenReturn(Optional.of("stored-hash"));
        when(passwordEncoder.matches("secret", "stored-hash")).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(requester));
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));

        User deleted = authentication.deleteUser(userId, email, "secret");

        assertEquals(userId, deleted.getId());
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserRejectsDeletingAnotherUser() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        UUID targetUserId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        User requester = user(requesterId);
        String email = "requester@example.com";
        requester.setEmail(email);
        requester.setPasswordHash("stored-hash");

        when(userRepository.findPasswordByEmail(email)).thenReturn(Optional.of("stored-hash"));
        when(passwordEncoder.matches("secret", "stored-hash")).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(requester));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> authentication.deleteUser(targetUserId, email, "secret")
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void logoutWithAccessTokenMarksUserOffline() {
        Authentication authentication = new Authentication(userRepository, passwordEncoder, jwtService);
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        user.setOnline(true);

        JwtService.TokenClaims claims = new JwtService.TokenClaims(
            userId,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType(),
            "access",
            Instant.now().plusSeconds(300),
            new AuthenticatedUser(userId, user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserType())
        );

        when(jwtService.parseAccessToken("access-token")).thenReturn(claims);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        authentication.logout("access-token");

        assertTrue(!user.getOnline());
        verify(userRepository).save(user);
    }

    @Test
    void updateAchievementRequiresAchievementId() {
        UUID userId = UUID.randomUUID();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> dashboardService.updateAchievement(
                userId,
                null,
                new AchievementRequest("Title", "Description", true, Month.APRIL)
            )
        );

        assertEquals("Achievement ID is required.", exception.getMessage());
    }

    private User user(UUID userId) {
        return new User(userId, UserType.PATIENT, "Test", "User", "test@example.com", "5551234567", "hash");
    }

    private DiaryEntry diaryEntry(User user, int moodBefore, int moodAfter, LocalDateTime createdAt) {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(UUID.randomUUID());
        entry.setUser(user);
        entry.setMoodBefore(moodBefore);
        entry.setMoodAfter(moodAfter);
        entry.setCreatedAt(createdAt);
        entry.setDeleted(false);
        return entry;
    }

    private CBTSession userSessionRecord(String userId, SessionStatus status, LocalDateTime endedAt) {
        CBTSession session = new CBTSession();
        session.setId(UUID.randomUUID());
        session.setUserId(userId);
        session.setStatus(status);
        session.setEndedAt(endedAt);
        return session;
    }

    private CBTSession librarySession(Long sessionId, String title) {
        CBTSession session = new CBTSession();
        session.setId(UUID.randomUUID());
        session.setSessionId(sessionId);
        session.setTitle(title);
        session.setDescription("Description");
        session.setDurationMinutes(20);
        session.setOrderIndex(1);
        return session;
    }

    private UserSession activeUserSession(User user, CBTSession session) {
        UserSession userSession = new UserSession();
        userSession.setId(UUID.randomUUID());
        userSession.setUser(user);
        userSession.setCbtSession(session);
        userSession.setStatus(UserSessionStatus.IN_PROGRESS);
        userSession.setStartedAt(LocalDateTime.now().minusMinutes(15));
        return userSession;
    }
}
