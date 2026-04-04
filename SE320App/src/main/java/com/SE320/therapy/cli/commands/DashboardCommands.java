package com.SE320.therapy.cli.commands;

import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.controller.DashboardController;
import com.SE320.therapy.dto.AchievementRequest;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.entity.Achievement;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.ProgressPoint;
import com.SE320.therapy.objects.UserType;

@Component
public class DashboardCommands implements Command {
    private final DashboardController dashboardController;
    private final UserCommands userCommands;
    private final Scanner scanner;

    public DashboardCommands(DashboardController dashboardController,
                             UserCommands userCommands,
                             Scanner scanner) {
        this.dashboardController = dashboardController;
        this.userCommands = userCommands;
        this.scanner = scanner;
    }
 
    @Override
    public void execute() {
        boolean running = true;

        printMenu();

        while (running) {
            System.out.print("Dashboard command: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "view" -> handleViewDashboard();
                case "2", "achievements", "list" -> handleListAchievements();
                case "3", "create" -> handleCreateAchievement();
                case "4", "update" -> handleUpdateAchievement();
                case "5", "delete" -> handleDeleteAchievement();
                case "help" -> printMenu();
                case "6", "back" -> running = false;
                default -> System.out.println("Please choose a valid dashboard option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Dashboard Menu ===");
        System.out.println("1. view");
        System.out.println("2. achievements");
        System.out.println("3. create");
        System.out.println("4. update");
        System.out.println("5. delete");
        System.out.println("6. back");
        System.out.println("Patients can only read their own dashboard.");
        System.out.println("Doctors can view any patient's dashboard and manage achievements.");
        System.out.println("Admins can manage achievements, but cannot view dashboard patient information.");
        System.out.println("Type a command name, number, or help.");
        System.out.println();
    }

    private void handleViewDashboard() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (currentUserId == null || currentUserType == null) {
            System.out.println("You must be logged in to access dashboard commands.");
            return;
        }

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to access dashboard patient information.");
            return;
        }

        try {
            UUID targetUserId = resolveDashboardUserId(currentUserId, currentUserType);
            if (targetUserId == null) {
                return;
            }

            Dashboard dashboard = dashboardController.getDashboard(targetUserId);
            printDashboard(dashboard, targetUserId, currentUserId.equals(targetUserId));
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load dashboard: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load dashboard right now.");
        }
    }

    private void handleListAchievements() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (currentUserId == null || currentUserType == null) {
            System.out.println("You must be logged in to access achievements.");
            return;
        }

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to view dashboard patient information.");
            return;
        }

        try {
            UUID targetUserId = resolveDashboardUserId(currentUserId, currentUserType);
            if (targetUserId == null) {
                return;
            }

            List<AchievementResponse> achievements = dashboardController.getAchievements(targetUserId);
            printAchievementResponses(achievements, targetUserId);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load achievements: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load achievements right now.");
        }
    }

    private void handleCreateAchievement() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (!canManageAchievements(currentUserId, currentUserType)) {
            return;
        }

        try {
            UUID targetUserId = readRequiredUuid("User ID for new achievement: ");
            AchievementRequest request = readAchievementRequest();
            AchievementResponse response = dashboardController.createAchievement(targetUserId, request);

            System.out.println("Achievement created successfully.");
            System.out.println(response.toString());
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to create achievement: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to create achievement right now.");
        }
    }

    private void handleUpdateAchievement() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (!canManageAchievements(currentUserId, currentUserType)) {
            return;
        }

        try {
            UUID targetUserId = readRequiredUuid("User ID for achievement update: ");
            UUID achievementId = readRequiredUuid("Achievement ID: ");
            AchievementRequest request = readAchievementRequest();
            AchievementResponse response = dashboardController.updateAchievement(targetUserId, achievementId, request);

            System.out.println("Achievement updated successfully.");
            System.out.println(response.toString());
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to update achievement: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to update achievement right now.");
        }
    }

    private void handleDeleteAchievement() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (!canManageAchievements(currentUserId, currentUserType)) {
            return;
        }

        try {
            UUID targetUserId = readRequiredUuid("User ID for achievement deletion: ");
            UUID achievementId = readRequiredUuid("Achievement ID: ");
            dashboardController.deleteAchievement(targetUserId, achievementId);
            System.out.println("Achievement deleted successfully.");
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to delete achievement: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to delete achievement right now.");
        }
    }

    private boolean canManageAchievements(UUID currentUserId, UserType currentUserType) {
        if (currentUserId == null || currentUserType == null) {
            System.out.println("You must be logged in to manage achievements.");
            return false;
        }

        if (currentUserType == UserType.PATIENT) {
            System.out.println("Patients have read-only dashboard access.");
            return false;
        }

        return true;
    }

    private UUID resolveDashboardUserId(UUID currentUserId, UserType currentUserType) {
        if (currentUserType == UserType.PATIENT) {
            return currentUserId;
        }

        System.out.print("Patient ID to view (leave blank for your own dashboard): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return currentUserId;
        }

        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid UUID.");
            return null;
        }
    }

    private AchievementRequest readAchievementRequest() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        boolean unlocked = readBoolean("Unlocked (true/false): ");
        Month unlockedMonth = unlocked ? readMonth() : null;
        return new AchievementRequest(title, description, unlocked, unlockedMonth);
    }

    private UUID readRequiredUuid(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException ignored) {
                System.out.println("Please enter a valid UUID.");
            }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            if ("true".equals(input) || "yes".equals(input) || "y".equals(input)) {
                return true;
            }

            if ("false".equals(input) || "no".equals(input) || "n".equals(input)) {
                return false;
            }

            System.out.println("Please enter true/false, yes/no, or y/n.");
        }
    }

    private Month readMonth() {
        while (true) {
            System.out.print("Unlocked month (for example JANUARY): ");
            String input = scanner.nextLine().trim();

            try {
                return Month.valueOf(input.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                System.out.println("Please enter a valid month name.");
            }
        }
    }

    private void printDashboard(Dashboard dashboard, UUID targetUserId, boolean ownDashboard) {
        System.out.println();
        System.out.println(ownDashboard ? "=== Your Dashboard ===" : "=== Dashboard for " + targetUserId + " ===");
        System.out.println("Month: " + dashboard.getMonthlyTrends().getCurrentMonth());
        System.out.printf("Average mood score: %.2f%n", dashboard.getMonthlyTrends().getAverageMoodScore());
        System.out.println("Sessions completed: " + dashboard.getMonthlyTrends().getSessionsCompleted());
        System.out.println("Journal entries this month: " + dashboard.getMonthlyTrends().getJournalEntriesThisMonth());
        System.out.printf("Improvement rate: %.2f%n", dashboard.getMonthlyTrends().getImprovementRate());
        System.out.println("Weekly goals completed: "
                + dashboard.getWeeklyProgress().getCompletedGoals()
                + "/"
                + dashboard.getWeeklyProgress().getTotalGoals());
        System.out.println("Current streak: " + dashboard.getWeeklyProgress().getCurrentStreak());
        System.out.println("Weekly progress points:");

        for (ProgressPoint progressPoint : dashboard.getWeeklyProgress().getProgressPoints()) {
            System.out.println("  " + progressPoint.getLabel() + ": " + progressPoint.getValue());
        }

        System.out.println("Achievements:");
        for (Achievement achievement : dashboard.getAchievements()) {
            System.out.println(achievement.toString());
        }
    }

    private void printAchievementResponses(List<AchievementResponse> achievements, UUID targetUserId) {
        if (achievements.isEmpty()) {
            System.out.println("No achievements found for user " + targetUserId + ".");
            return;
        }

        System.out.println();
        System.out.println("Achievements for " + targetUserId + ":");
        for (AchievementResponse achievement : achievements) {
            System.out.println(achievement.toString());
        }
    }
}
