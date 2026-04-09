package com.SE320.therapy.cli.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.controller.DashboardController;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.dto.objects.BurnoutRecovery;
import com.SE320.therapy.dto.objects.Dashboard;
import com.SE320.therapy.dto.objects.MonthlyTrends;
import com.SE320.therapy.dto.objects.UserType;
import com.SE320.therapy.dto.objects.WeeklyProgress;

class DashboardCommandsTest extends MenuTestSupport {

    @Test
    void execute_viewDashboard_requiresLoggedInUser() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(null);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("1\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to access dashboard commands."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewDashboard_blocksAdminAccess() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("1\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Admins are not allowed to access dashboard patient information."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewDashboard_repromptsDoctorUntilValidPatientId() {
        UUID doctorId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID patientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);
        when(dashboardController.getDashboard(patientId)).thenReturn(buildDashboard(patientId));

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom("1\n\nnot-a-uuid\n" + patientId + "\n9\n")
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Doctors must enter a patient UUID."));
        assertTrue(output.contains("Please enter a valid UUID."));
        assertTrue(output.contains("=== Dashboard for " + patientId + " ==="));
        verify(dashboardController, times(1)).getDashboard(patientId);
    }

    @Test
    void execute_viewDashboard_deniesWhenDashboardIsNotViewable() {
        UUID patientId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID otherOwner = UUID.fromString("44444444-4444-4444-4444-444444444444");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getDashboard(patientId)).thenReturn(buildDashboard(otherOwner));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("1\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You are not allowed to view that dashboard."));
        verify(dashboardController, times(1)).getDashboard(patientId);
    }

    @Test
    void execute_viewDashboard_handlesIllegalArgumentException() {
        UUID patientId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getDashboard(patientId)).thenThrow(new IllegalArgumentException("bad request"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("1\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load dashboard: bad request"));
    }

    @Test
    void execute_viewDashboard_handlesResponseStatusException() {
        UUID patientId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getDashboard(patientId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "missing"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("1\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load dashboard: 404 NOT_FOUND \"missing\""));
    }

    @Test
    void execute_viewDashboard_handlesUnexpectedException() {
        UUID patientId = UUID.fromString("77777777-7777-7777-7777-777777777777");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getDashboard(patientId)).thenThrow(new RuntimeException("boom"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("1\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load dashboard right now."));
    }

    @Test
    void execute_viewMonthlyTrends_requiresLoggedInUser() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(null);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("2\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to access dashboard commands."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewMonthlyTrends_blocksAdminAccess() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("2\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Admins are not allowed to access dashboard patient information."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewMonthlyTrends_repromptsDoctorUntilValidPatientId() {
        UUID doctorId = UUID.fromString("12121212-1212-1212-1212-121212121212");
        UUID patientId = UUID.fromString("34343434-3434-3434-3434-343434343434");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);
        when(dashboardController.getMonthlyTrends(patientId)).thenReturn(new MonthlyTrends());

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom("2\n\ninvalid\n" + patientId + "\n9\n")
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Doctors must enter a patient UUID."));
        assertTrue(output.contains("Please enter a valid UUID."));
        assertTrue(output.contains("=== Monthly Trends for " + patientId + " ==="));
        verify(dashboardController, times(1)).getMonthlyTrends(patientId);
    }

    @Test
    void execute_viewMonthlyTrends_handlesIllegalArgumentException() {
        UUID patientId = UUID.fromString("88888888-8888-8888-8888-888888888888");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getMonthlyTrends(patientId)).thenThrow(new IllegalArgumentException("bad month"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("2\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load monthly trends: bad month"));
    }

    @Test
    void execute_viewMonthlyTrends_handlesResponseStatusException() {
        UUID patientId = UUID.fromString("89898989-8989-8989-8989-898989898989");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getMonthlyTrends(patientId))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid month range"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("2\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load monthly trends: 400 BAD_REQUEST \"invalid month range\""));
    }

    @Test
    void execute_viewMonthlyTrends_handlesUnexpectedException() {
        UUID patientId = UUID.fromString("90909090-9090-9090-9090-909090909090");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getMonthlyTrends(patientId)).thenThrow(new RuntimeException("boom"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("2\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load monthly trends right now."));
    }

    @Test
    void execute_viewWeeklyProgress_requiresLoggedInUser() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(null);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("3\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to access dashboard commands."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewWeeklyProgress_blocksAdminAccess() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("3\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Admins are not allowed to access dashboard patient information."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewWeeklyProgress_repromptsDoctorUntilValidPatientId() {
        UUID doctorId = UUID.fromString("13131313-1313-1313-1313-131313131313");
        UUID patientId = UUID.fromString("35353535-3535-3535-3535-353535353535");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);
        when(dashboardController.getWeeklyProgress(patientId)).thenReturn(new WeeklyProgress());

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom("3\n\ninvalid\n" + patientId + "\n9\n")
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Doctors must enter a patient UUID."));
        assertTrue(output.contains("Please enter a valid UUID."));
        assertTrue(output.contains("=== Weekly Progress for " + patientId + " ==="));
        verify(dashboardController, times(1)).getWeeklyProgress(patientId);
    }

    @Test
    void execute_viewWeeklyProgress_handlesIllegalArgumentException() {
        UUID patientId = UUID.fromString("91919191-9191-9191-9191-919191919191");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getWeeklyProgress(patientId)).thenThrow(new IllegalArgumentException("bad week"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("3\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load weekly progress: bad week"));
    }

    @Test
    void execute_viewWeeklyProgress_handlesResponseStatusException() {
        UUID patientId = UUID.fromString("92929292-9292-9292-9292-929292929292");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getWeeklyProgress(patientId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "weekly data missing"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("3\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load weekly progress: 404 NOT_FOUND \"weekly data missing\""));
    }

    @Test
    void execute_viewWeeklyProgress_handlesUnexpectedException() {
        UUID patientId = UUID.fromString("93939393-9393-9393-9393-939393939393");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getWeeklyProgress(patientId)).thenThrow(new RuntimeException("boom"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("3\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load weekly progress right now."));
    }

    @Test
    void execute_viewBurnoutRecovery_requiresLoggedInUser() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(null);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("4\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to access dashboard commands."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewBurnoutRecovery_blocksAdminAccess() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("4\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Admins are not allowed to access dashboard patient information."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_viewBurnoutRecovery_repromptsDoctorUntilValidPatientId() {
        UUID doctorId = UUID.fromString("14141414-1414-1414-1414-141414141414");
        UUID patientId = UUID.fromString("45454545-4545-4545-4545-454545454545");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);
        when(dashboardController.getBurnoutRecovery(patientId)).thenReturn(new BurnoutRecovery());

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom("4\n\ninvalid\n" + patientId + "\n9\n")
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Doctors must enter a patient UUID."));
        assertTrue(output.contains("Please enter a valid UUID."));
        assertTrue(output.contains("=== Burnout Recovery for " + patientId + " ==="));
        verify(dashboardController, times(1)).getBurnoutRecovery(patientId);
    }

    @Test
    void execute_viewBurnoutRecovery_handlesIllegalArgumentException() {
        UUID patientId = UUID.fromString("94949494-9494-9494-9494-949494949494");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getBurnoutRecovery(patientId)).thenThrow(new IllegalArgumentException("bad burnout data"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("4\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load burnout recovery details: bad burnout data"));
    }

    @Test
    void execute_viewBurnoutRecovery_handlesResponseStatusException() {
        UUID patientId = UUID.fromString("95959595-9595-9595-9595-959595959595");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getBurnoutRecovery(patientId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "burnout data missing"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("4\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load burnout recovery details: 404 NOT_FOUND \"burnout data missing\""));
    }

    @Test
    void execute_viewBurnoutRecovery_handlesUnexpectedException() {
        UUID patientId = UUID.fromString("96969696-9696-9696-9696-969696969696");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getBurnoutRecovery(patientId)).thenThrow(new RuntimeException("boom"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("4\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load burnout recovery details right now."));
    }

    @Test
    void execute_listAchievements_requiresLoggedInUser() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(null);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("5\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to access achievements."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_listAchievements_blocksAdminAccess() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("5\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Admins are not allowed to view dashboard patient information."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_listAchievements_showsEmptyStateForPatient() {
        UUID patientId = UUID.fromString("97979797-9797-9797-9797-979797979797");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getAchievements(patientId)).thenReturn(List.of());

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("5\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("No achievements found for user " + patientId + "."));
        verify(dashboardController, times(1)).getAchievements(patientId);
    }

    @Test
    void execute_listAchievements_handlesIllegalArgumentException() {
        UUID patientId = UUID.fromString("98989898-9898-9898-9898-989898989898");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getAchievements(patientId)).thenThrow(new IllegalArgumentException("bad achievements request"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("5\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load achievements: bad achievements request"));
    }

    @Test
    void execute_listAchievements_handlesResponseStatusException() {
        UUID patientId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getAchievements(patientId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "achievements missing"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("5\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load achievements: 404 NOT_FOUND \"achievements missing\""));
    }

    @Test
    void execute_listAchievements_handlesUnexpectedException() {
        UUID patientId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(patientId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);
        when(dashboardController.getAchievements(patientId)).thenThrow(new RuntimeException("boom"));

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("5\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to load achievements right now."));
    }

    @Test
    void execute_createAchievement_requiresLoggedInUser() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(null);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("6\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("You must be logged in to manage achievements."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_createAchievement_blocksPatientAccess() {
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
        when(userCommands.getCurrentUserType()).thenReturn(UserType.PATIENT);

        DashboardCommands commands = new DashboardCommands(dashboardController, userCommands, scannerFrom("6\n9\n"));
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Patients have read-only dashboard access."));
        verifyNoInteractions(dashboardController);
    }

    @Test
    void execute_createAchievement_retriesInvalidInputs_thenCreatesForDoctor() {
        UUID doctorId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UUID patientId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        UUID achievementId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);
        when(dashboardController.createAchievement(any(), any())).thenReturn(
                new AchievementResponse(achievementId, patientId, "Consistency", "Great work", true, Month.JANUARY)
        );

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom(
                        "6\n" +
                        "not-a-uuid\n" +
                        patientId + "\n" +
                        "Consistency\n" +
                        "Great work\n" +
                        "maybe\n" +
                        "yes\n" +
                        "NotAMonth\n" +
                        "JANUARY\n" +
                        "9\n"
                )
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Please enter a valid UUID."));
        assertTrue(output.contains("Please enter true/false, yes/no, or y/n."));
        assertTrue(output.contains("Please enter a valid month name."));
        assertTrue(output.contains("Achievement created successfully."));
        verify(dashboardController, times(1)).createAchievement(
                argThat(userId -> userId.equals(patientId)),
                argThat(request ->
                        "Consistency".equals(request.getTitle())
                                && "Great work".equals(request.getDescription())
                                && request.isUnlocked()
                                && Month.JANUARY.equals(request.getUnlockedMonth()))
        );
    }

    @Test
    void execute_createAchievement_setsUnlockedMonthToNullWhenLocked() {
        UUID adminId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        UUID patientId = UUID.fromString("abababab-abab-abab-abab-abababababab");
        UUID achievementId = UUID.fromString("cdcdcdcd-cdcd-cdcd-cdcd-cdcdcdcdcdcd");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(adminId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);
        when(dashboardController.createAchievement(any(), any())).thenReturn(
                new AchievementResponse(achievementId, patientId, "Locked", "Keep going", false, null)
        );

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom(
                        "6\n" +
                        patientId + "\n" +
                        "Locked\n" +
                        "Keep going\n" +
                        "false\n" +
                        "9\n"
                )
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Achievement created successfully."));
        verify(dashboardController, times(1)).createAchievement(
                argThat(userId -> userId.equals(patientId)),
                argThat(request -> !request.isUnlocked() && request.getUnlockedMonth() == null)
        );
    }

    @Test
    void execute_createAchievement_handlesIllegalArgumentException() {
        UUID adminId = UUID.fromString("edededed-eded-eded-eded-edededededed");
        UUID patientId = UUID.fromString("fefefefe-fefe-fefe-fefe-fefefefefefe");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(adminId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);
        when(dashboardController.createAchievement(any(), any())).thenThrow(new IllegalArgumentException("duplicate title"));

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom(
                        "6\n" +
                        patientId + "\n" +
                        "Title\n" +
                        "Description\n" +
                        "false\n" +
                        "9\n"
                )
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to create achievement: duplicate title"));
    }

    @Test
    void execute_createAchievement_handlesResponseStatusException() {
        UUID adminId = UUID.fromString("acacacac-acac-acac-acac-acacacacacac");
        UUID patientId = UUID.fromString("bdbdbdbd-bdbd-bdbd-bdbd-bdbdbdbdbdbd");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(adminId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.ADMIN);
        when(dashboardController.createAchievement(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid achievement payload"));

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom(
                        "6\n" +
                        patientId + "\n" +
                        "Title\n" +
                        "Description\n" +
                        "false\n" +
                        "9\n"
                )
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to create achievement: 400 BAD_REQUEST \"invalid achievement payload\""));
    }

    @Test
    void execute_createAchievement_handlesUnexpectedException() {
        UUID doctorId = UUID.fromString("cececece-cece-cece-cece-cececececece");
        UUID patientId = UUID.fromString("dfdfdfdf-dfdf-dfdf-dfdf-dfdfdfdfdfdf");
        DashboardController dashboardController = mock(DashboardController.class);
        UserCommands userCommands = mock(UserCommands.class);
        when(userCommands.getCurrentUserId()).thenReturn(doctorId);
        when(userCommands.getCurrentUserType()).thenReturn(UserType.DOCTOR);
        when(dashboardController.createAchievement(any(), any())).thenThrow(new RuntimeException("boom"));

        DashboardCommands commands = new DashboardCommands(
                dashboardController,
                userCommands,
                scannerFrom(
                        "6\n" +
                        patientId + "\n" +
                        "Title\n" +
                        "Description\n" +
                        "false\n" +
                        "9\n"
                )
        );
        commands.execute();

        String output = getOutput();
        assertTrue(output.contains("Unable to create achievement right now."));
    }

    private Dashboard buildDashboard(UUID ownerId) {
        return new Dashboard(
                ownerId,
                new MonthlyTrends(),
                new WeeklyProgress(),
                new BurnoutRecovery(),
                List.of()
        );
    }
}
