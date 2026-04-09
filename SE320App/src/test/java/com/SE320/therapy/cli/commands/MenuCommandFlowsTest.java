package com.SE320.therapy.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import com.SE320.therapy.controller.CrisisController;
import com.SE320.therapy.controller.DashboardController;
import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.controller.UserController;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.UserResponse;
import com.SE320.therapy.objects.BurnoutRecovery;
import com.SE320.therapy.objects.Crisis;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.MaslachBurnoutInventoryDimensions;
import com.SE320.therapy.objects.MonthlyTrends;
import com.SE320.therapy.objects.ProgressPoint;
import com.SE320.therapy.objects.SeverityLevel;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.objects.WeeklyProgress;
import com.SE320.therapy.service.AuthResponse;
import com.SE320.therapy.service.DiaryService;

class MenuCommandFlowsTest extends MenuTestSupport {

    @Test
    void commands_handlesUserOps() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UserController userController = mock(UserController.class);
        UserResponse user = new UserResponse(
                userId,
                UserType.PATIENT,
                "Jane",
                "Doe",
                "jane@example.com",
                "5551234",
                true
        );
        UserResponse deletedUser = new UserResponse(
                userId,
                UserType.PATIENT,
                "Jane",
                "Doe",
                "jane@example.com",
                "5551234",
                false
        );
        when(userController.register(any())).thenReturn(new AuthResponse("access-register", "refresh-register", user));
        when(userController.login(any())).thenReturn(new AuthResponse("access-login", "refresh-login", user));
        when(userController.refresh(any())).thenReturn(new AuthResponse("access-refreshed", "refresh-refreshed", user));
        when(userController.delete(any())).thenReturn(deletedUser);

        Menu menu = createMenu(
                new UserCommands(userController, scannerFrom(
                        "register\n" +
                        "PATIENT\n" +
                        "Jane\n" +
                        "Doe\n" +
                        "jane@example.com\n" +
                        "password123\n" +
                        "5551234\n" +
                        "session\n" +
                        "logout\n" +
                        "login\n" +
                        "jane@example.com\n" +
                        "password123\n" +
                        "delete\n" +
                        "jane@example.com\n" +
                        "password123\n" +
                        "help\n" +
                        "bad-option\n" +
                        "back\n"
                )),
                createSessionCommands("back\n"),
                createDiaryCommands("back\n"),
                createDashboardCommands("back\n"),
                createCrisisCommands("back\n"),
                new SettingsCommands(),
                "auth\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== User Menu ==="));
        assertTrue(output.contains("Registration successful."));
        assertTrue(output.contains("Login successful."));
        assertTrue(output.contains("Logout successful."));
        assertTrue(output.contains("Account deleted successfully."));
        assertTrue(output.contains("Access token: access-refreshed"));
        assertTrue(output.contains("Email: jane@example.com"));
        assertTrue(output.contains("Please choose a valid menu option."));
        assertEquals(2, countOccurrences(output, "=== User Menu ==="));
        verify(userController, times(1)).register(any());
        verify(userController, times(1)).login(any());
        verify(userController, times(3)).refresh(any());
        verify(userController, times(1)).logout("Bearer access-refreshed");
        verify(userController, times(1)).delete(any());
    }

    @Test
    void commands_handlesDashboardOps() {
        UUID doctorId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID patientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID achievementId = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);

        Dashboard dashboard = new Dashboard(
                patientId,
                new MonthlyTrends(YearMonth.of(2025, 1), 4.2, 5, 3, 0.5),
                new WeeklyProgress(
                        DayOfWeek.MONDAY,
                        3,
                        4,
                        7,
                        List.of(new ProgressPoint("Mood", 4.0))
                ),
                new BurnoutRecovery(
                        new MaslachBurnoutInventoryDimensions(2.1, 1.3, 4.5),
                        List.of("Take short breaks"),
                        List.of("Protect evenings"),
                        List.of("Say no to extra tasks")
                ),
                List.of()
        );
        when(dashboardController.getDashboard(patientId)).thenReturn(dashboard);
        when(dashboardController.getMonthlyTrends(patientId))
                .thenReturn(new MonthlyTrends(YearMonth.of(2025, 2), 4.0, 6, 4, 0.6));
        when(dashboardController.getWeeklyProgress(patientId))
                .thenReturn(new WeeklyProgress(
                        DayOfWeek.MONDAY,
                        4,
                        5,
                        8,
                        List.of(new ProgressPoint("Energy", 3.8))
                ));
        when(dashboardController.getBurnoutRecovery(patientId))
                .thenReturn(new BurnoutRecovery(
                        new MaslachBurnoutInventoryDimensions(2.0, 1.2, 4.6),
                        List.of("Schedule reflection time"),
                        List.of("Protect weekends"),
                        List.of("Set inbox limits")
                ));
        when(dashboardController.getAchievements(patientId)).thenReturn(List.of(
                new AchievementResponse(achievementId, patientId, "Consistency", "Completed sessions", true, Month.JANUARY)
        ));
        when(dashboardController.createAchievement(eq(patientId), any())).thenReturn(
                new AchievementResponse(achievementId, patientId, "New Achievement", "Celebrate small wins", true, Month.JANUARY)
        );
        when(dashboardController.updateAchievement(eq(patientId), eq(achievementId), any())).thenReturn(
                new AchievementResponse(achievementId, patientId, "Updated Achievement", "Keep momentum", false, null)
        );

        Menu menu = createMenu(
                createUserCommands("back\n"),
                createSessionCommands("back\n"),
                createDiaryCommands("back\n"),
                new DashboardCommands(dashboardController, userCommands, scannerFrom(
                        "1\n" +
                        patientId + "\n" +
                        "2\n" +
                        patientId + "\n" +
                        "3\n" +
                        patientId + "\n" +
                        "4\n" +
                        patientId + "\n" +
                        "5\n" +
                        patientId + "\n" +
                        "6\n" +
                        patientId + "\n" +
                        "New Achievement\n" +
                        "Celebrate small wins\n" +
                        "true\n" +
                        "JANUARY\n" +
                        "7\n" +
                        patientId + "\n" +
                        achievementId + "\n" +
                        "Updated Achievement\n" +
                        "Keep momentum\n" +
                        "false\n" +
                        "8\n" +
                        patientId + "\n" +
                        achievementId + "\n" +
                        "help\n" +
                        "bad\n" +
                        "9\n"
                )),
                createCrisisCommands("back\n"),
                new SettingsCommands(),
                "dashboard\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Dashboard Menu ==="));
        assertTrue(output.contains("Dashboard command: "));
        assertTrue(output.contains("=== Dashboard for " + patientId + " ==="));
        assertTrue(output.contains("=== Monthly Trends for " + patientId + " ==="));
        assertTrue(output.contains("=== Weekly Progress for " + patientId + " ==="));
        assertTrue(output.contains("=== Burnout Recovery for " + patientId + " ==="));
        assertTrue(output.contains("Achievements for " + patientId + ":"));
        assertTrue(output.contains("Achievement created successfully."));
        assertTrue(output.contains("Achievement updated successfully."));
        assertTrue(output.contains("Achievement deleted successfully."));
        assertTrue(output.contains("Please choose a valid dashboard option."));
        assertEquals(2, countOccurrences(output, "=== Dashboard Menu ==="));
        verify(dashboardController, times(1)).getDashboard(patientId);
        verify(dashboardController, times(1)).getMonthlyTrends(patientId);
        verify(dashboardController, times(1)).getWeeklyProgress(patientId);
        verify(dashboardController, times(1)).getBurnoutRecovery(patientId);
        verify(dashboardController, times(1)).getAchievements(patientId);
        verify(dashboardController, times(1)).createAchievement(eq(patientId), any());
        verify(dashboardController, times(1)).updateAchievement(eq(patientId), eq(achievementId), any());
        verify(dashboardController, times(1)).deleteAchievement(patientId, achievementId);
    }

    @Test
    void commands_handlesCrisisOps() {
        UUID doctorId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID patientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        CrisisController crisisController = mock(CrisisController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);

        when(crisisController.getCrisisHub(patientId)).thenReturn(new Crisis(
                patientId,
                List.of("Racing thoughts"),
                List.of("Box breathing"),
                List.of("Call support person"),
                List.of("988")
        ));
        when(crisisController.getCopingStrategies()).thenReturn(List.of("Grounding exercise"));
        when(crisisController.getSafetyPlan(patientId)).thenReturn(List.of("Move to safe location"));
        when(crisisController.detectCrisisIndicators(any())).thenReturn(new CrisisDetectionResponse(
                true,
                SeverityLevel.SIGNIFICANT,
                List.of("self-harm language"),
                List.of("Contact emergency support")
        ));
        when(crisisController.updateSafetyPlan(eq(patientId), any())).thenReturn(List.of(
                "Text trusted friend",
                "Call therapist"
        ));

        Menu menu = createMenu(
                createUserCommands("back\n"),
                createSessionCommands("back\n"),
                createDiaryCommands("back\n"),
                createDashboardCommands("back\n"),
                new CrisisCommands(crisisController, userCommands, scannerFrom(
                        "1\n" +
                        patientId + "\n" +
                        "2\n" +
                        "3\n" +
                        patientId + "\n" +
                        "4\n" +
                        "I don't feel safe right now\n" +
                        "self-harm language\n" +
                        "\n" +
                        "5\n" +
                        patientId + "\n" +
                        "Text trusted friend\n" +
                        "Call therapist\n" +
                        "\n" +
                        "help\n" +
                        "bad\n" +
                        "6\n"
                )),
                new SettingsCommands(),
                "crisis\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Crisis Menu ==="));
        assertTrue(output.contains("Crisis command: "));
        assertTrue(output.contains("=== Crisis Hub for " + patientId + " ==="));
        assertTrue(output.contains("Coping Strategies:"));
        assertTrue(output.contains("Safety Plan for " + patientId + ":"));
        assertTrue(output.contains("=== Crisis Detection Result ==="));
        assertTrue(output.contains("Crisis detected: true"));
        assertTrue(output.contains("Safety plan updated successfully."));
        assertTrue(output.contains("Please choose a valid crisis option."));
        assertEquals(2, countOccurrences(output, "=== Crisis Menu ==="));
        verify(crisisController, times(1)).getCrisisHub(patientId);
        verify(crisisController, times(1)).getCopingStrategies();
        verify(crisisController, times(1)).getSafetyPlan(patientId);
        verify(crisisController, times(1)).detectCrisisIndicators(any());
        verify(crisisController, times(1)).updateSafetyPlan(eq(patientId), any());
    }

    @Test
    void commands_handlesCrisisHubAndCopingEdgeCases() {
        UUID adminId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID patientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID otherPatientId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        CrisisController crisisController = mock(CrisisController.class);
        UserCommands userCommands = mock(UserCommands.class);

        when(userCommands.getCurrentUserId()).thenReturn(null, adminId, patientId, patientId);
        when(userCommands.getCurrentUserType()).thenReturn(null, UserType.ADMIN, UserType.PATIENT, UserType.PATIENT);
        when(crisisController.getCrisisHub(patientId))
                .thenReturn(new Crisis(
                        otherPatientId,
                        List.of("Restlessness"),
                        List.of("Box breathing"),
                        List.of("Call support"),
                        List.of("988")
                ))
                .thenThrow(new IllegalArgumentException("not found"));
        when(crisisController.getCopingStrategies())
                .thenThrow(new IllegalArgumentException("invalid strategy source"))
                .thenThrow(new RuntimeException("service unavailable"));

        Menu menu = createMenu(
                createUserCommands("back\n"),
                createSessionCommands("back\n"),
                createDiaryCommands("back\n"),
                createDashboardCommands("back\n"),
                new CrisisCommands(crisisController, userCommands, scannerFrom(
                        "1\n" +
                        "1\n" +
                        "1\n" +
                        "1\n" +
                        "2\n" +
                        "2\n" +
                        "6\n"
                )),
                new SettingsCommands(),
                "crisis\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to access crisis commands."));
        assertTrue(output.contains("Admins are not allowed to access crisis patient information."));
        assertTrue(output.contains("You are not allowed to view that crisis hub."));
        assertTrue(output.contains("Unable to load crisis hub: not found"));
        assertTrue(output.contains("Unable to load coping strategies: invalid strategy source"));
        assertTrue(output.contains("Unable to load coping strategies right now."));
        verify(crisisController, times(2)).getCrisisHub(patientId);
        verify(crisisController, times(2)).getCopingStrategies();
    }

    @Test
    void commands_handlesSessionOps() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        SessionController sessionController = mock(SessionController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(userId);
        when(sessionController.getSessionLibrary()).thenReturn(List.of(
                new SessionLibraryItemResponse(1001L, "Thought Record", "desc", 20, 1, List.of("COGNITIVE")),
                new SessionLibraryItemResponse(1002L, "Behavioral Activation", "desc", 20, 2, List.of("BEHAVIORAL"))
        ));
        when(sessionController.startSession(eq(1001L), any())).thenReturn(new SessionRunResponse(
                UUID.randomUUID(),
                userId,
                1001L,
                "Thought Record",
                "IN_PROGRESS",
                1,
                null,
                LocalDateTime.now(),
                null
        ));
        when(sessionController.continueSession(userId, 42L)).thenReturn(new SessionRunResponse(
                UUID.randomUUID(),
                userId,
                42L,
                "Behavioral Activation",
                "IN_PROGRESS",
                2,
                null,
                LocalDateTime.now().minusMinutes(5),
                null
        ));
        when(sessionController.getSessionHistory(userId)).thenReturn(List.of(
                new SessionRunResponse(
                        UUID.randomUUID(),
                        userId,
                        42L,
                        "Behavioral Activation",
                        "IN_PROGRESS",
                        2,
                        null,
                        LocalDateTime.now().minusMinutes(5),
                        null
                )
        ));

        Menu menu = createMenu(
                userCommands,
                new SessionCommands(sessionController, scannerFrom(
                        "1\n" +
                        "2\n" +
                        "1\n" +
                        "pause\n" +
                        "3\n" +
                        "42\n" +
                        "pause\n" +
                        "4\n" +
                        "42\n" +
                        "5\n" +
                        "help\n" +
                        "bad\n" +
                        "6\n"
                ), userCommands),
                createDiaryCommands("back\n"),
                createDashboardCommands("back\n"),
                createCrisisCommands("back\n"),
                new SettingsCommands(),
                "session\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Session Menu ==="));
        assertTrue(output.contains("Session command: "));
        assertTrue(output.contains("--- Session Library ---"));
        assertTrue(output.contains("--- Start New Session ---"));
        assertTrue(output.contains("New CBT session started successfully."));
        assertTrue(output.contains("Session continued successfully."));
        assertTrue(output.contains("--- Available Sessions ---"));
        assertTrue(output.contains("Session ended successfully."));
        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("Please choose a valid session option."));
        assertEquals(2, countOccurrences(output, "=== Session Menu ==="));
        verify(sessionController, times(2)).getSessionLibrary();
        verify(sessionController, times(1)).startSession(eq(1001L), any());
        verify(sessionController, times(1)).continueSession(userId, 42L);
        verify(sessionController, times(2)).getSessionHistory(userId);
        verify(sessionController, times(1)).endActiveSession(eq(42L), any());
    }

    @Test
    void commands_handlesDiaryOps() {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID entryId = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);
        DiaryService diaryService = mock(DiaryService.class);
        when(diaryService.suggestDistortions(any()))
                .thenReturn(List.of(new DistortionSuggestion("catastrophizing", 0.9, "All-or-nothing framing.")));
        when(diaryService.getEntries(eq(userId), any()))
                .thenReturn(new PageImpl<>(List.of(new DiaryEntrySummary(
                        entryId,
                        "A stressful meeting",
                        3,
                        6,
                        createdAt
                ))));
        when(diaryService.getEntryDetail(entryId))
                .thenReturn(new DiaryEntryDetail(
                        entryId,
                        userId,
                        "A stressful meeting",
                        "I always mess this up",
                        "I can prepare and improve",
                        3,
                        6,
                        createdAt
                ));
        when(diaryService.getInsights(userId)).thenReturn(new DiaryInsights(1, 3.0, 3));

        Menu menu = createMenu(
                createUserCommands("back\n"),
                createSessionCommands("back\n"),
                new DiaryCommands(scannerFrom(
                        "1\n" +
                        "A stressful meeting\n" +
                        "I always mess this up\n" +
                        "I can prepare and improve\n" +
                        "3\n" +
                        "6\n" +
                        "2\n" +
                        "1\n" +
                        "3\n" +
                        "help\n" +
                        "bad\n" +
                        "4\n"
                ), diaryService, () -> userId),
                createDashboardCommands("back\n"),
                createCrisisCommands("back\n"),
                new SettingsCommands(),
                "diary\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Diary Menu ==="));
        assertTrue(output.contains("Diary command: "));
        assertTrue(output.contains("Diary entry created successfully."));
        assertTrue(output.contains("Your Diary Entries:"));
        assertTrue(output.contains("Diary Entry Details"));
        assertTrue(output.contains("Diary Insights"));
        assertTrue(output.contains("Please choose a valid diary option."));
        assertEquals(2, countOccurrences(output, "=== Diary Menu ==="));
        verify(diaryService, times(1)).createEntry(eq(userId), any());
        verify(diaryService, times(1)).getEntries(eq(userId), any());
        verify(diaryService, times(1)).getEntryDetail(entryId);
        verify(diaryService, times(1)).getInsights(userId);
        verify(diaryService, times(1)).suggestDistortions(any());
    }
}
