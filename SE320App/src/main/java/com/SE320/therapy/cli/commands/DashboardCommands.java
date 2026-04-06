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
import com.SE320.therapy.objects.BurnoutRecovery;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.MaslachBurnoutInventoryDimensions;
import com.SE320.therapy.objects.MonthlyTrends;
import com.SE320.therapy.objects.ProgressPoint;
import com.SE320.therapy.objects.UserType;
import com.SE320.therapy.objects.WeeklyProgress;

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
                case "2", "monthly" -> handleViewMonthlyTrends();
                case "3", "weekly" -> handleViewWeeklyProgress();
                case "4", "burnout" -> handleViewBurnoutRecovery();
                case "5", "achievements", "list" -> handleListAchievements();
                case "6", "create" -> handleCreateAchievement();
                case "7", "update" -> handleUpdateAchievement();
                case "8", "delete" -> handleDeleteAchievement();
                case "help" -> printMenu();
                case "9", "back" -> running = false;
                default -> System.out.println("Please choose a valid dashboard option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Dashboard Menu ===");
        System.out.println("1. view");
        System.out.println("2. monthly");
        System.out.println("3. weekly");
        System.out.println("4. burnout");
        System.out.println("5. achievements");
        System.out.println("6. create");
        System.out.println("7. update");
        System.out.println("8. delete");
        System.out.println("9. back");
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
            if (!dashboard.canBeViewedBy(currentUserId, currentUserType)) {
                System.out.println("You are not allowed to view that dashboard.");
                return;
            }

            printDashboard(dashboard, dashboard.getOwnerUserId(), dashboard.isOwnedBy(currentUserId));
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load dashboard: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load dashboard right now.");
        }
    }

    private void handleViewMonthlyTrends() {
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

            MonthlyTrends monthlyTrends = dashboardController.getMonthlyTrends(targetUserId);
            printMonthlyTrends(monthlyTrends, targetUserId, currentUserId.equals(targetUserId));
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load monthly trends: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load monthly trends right now.");
        }
    }

    private void handleViewWeeklyProgress() {
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

            WeeklyProgress weeklyProgress = dashboardController.getWeeklyProgress(targetUserId);
            printWeeklyProgress(weeklyProgress, targetUserId, currentUserId.equals(targetUserId));
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load weekly progress: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load weekly progress right now.");
        }
    }

    private void handleViewBurnoutRecovery() {
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

            BurnoutRecovery burnoutRecovery = dashboardController.getBurnoutRecovery(targetUserId);
            printBurnoutRecovery(burnoutRecovery, targetUserId, currentUserId.equals(targetUserId));
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load burnout recovery details: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load burnout recovery details right now.");
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

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to access dashboard patient information.");
            return null;
        }

        while (true) {
            System.out.print("Patient ID to view: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Doctors must enter a patient UUID.");
                continue;
            }

            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid UUID.");
            }
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
        printMonthlyTrendsDetails(dashboard.getMonthlyTrends());
        printWeeklyProgressDetails(dashboard.getWeeklyProgress());
        printBurnoutRecoveryDetails(dashboard.getBurnoutRecovery());
        System.out.println("Achievements:");
        for (Achievement achievement : dashboard.getAchievements()) {
            System.out.println(achievement.toString());
        }
    }

    private void printMonthlyTrends(MonthlyTrends monthlyTrends, UUID targetUserId, boolean ownDashboard) {
        System.out.println();
        System.out.println(ownDashboard ? "=== Your Monthly Trends ===" : "=== Monthly Trends for " + targetUserId + " ===");
        printMonthlyTrendsDetails(monthlyTrends);
    }

    private void printMonthlyTrendsDetails(MonthlyTrends monthlyTrends) {
        System.out.println("Period: " + monthlyTrends.getPeriod());
        System.out.printf("Average mood score: %.2f%n", monthlyTrends.getAverageMoodScore());
        System.out.println("Sessions completed: " + monthlyTrends.getSessionsCompleted());
        System.out.println("Journal entries created: " + monthlyTrends.getJournalEntriesCreated());
        System.out.printf("Improvement rate: %.2f%n", monthlyTrends.getImprovementRate());
    }

    private void printWeeklyProgress(WeeklyProgress weeklyProgress, UUID targetUserId, boolean ownDashboard) {
        System.out.println();
        System.out.println(ownDashboard ? "=== Your Weekly Progress ===" : "=== Weekly Progress for " + targetUserId + " ===");
        printWeeklyProgressDetails(weeklyProgress);
    }

    private void printWeeklyProgressDetails(WeeklyProgress weeklyProgress) {
        System.out.println("Week starts on: " + weeklyProgress.getWeekStart());
        System.out.println("Weekly goals completed: "
                + weeklyProgress.getCompletedGoals()
                + "/"
                + weeklyProgress.getTotalGoals());
        System.out.println("Current streak: " + weeklyProgress.getCurrentStreak());
        System.out.println("Weekly progress points:");

        if (weeklyProgress.getProgressPoints().isEmpty()) {
            System.out.println("  No progress points available.");
            return;
        }

        for (ProgressPoint progressPoint : weeklyProgress.getProgressPoints()) {
            System.out.println("  " + progressPoint.getLabel() + ": " + progressPoint.getValue());
        }
    }

    private void printBurnoutRecovery(BurnoutRecovery burnoutRecovery, UUID targetUserId, boolean ownDashboard) {
        System.out.println();
        System.out.println(ownDashboard ? "=== Your Burnout Recovery ===" : "=== Burnout Recovery for " + targetUserId + " ===");
        printBurnoutRecoveryDetails(burnoutRecovery);
    }

    private void printBurnoutRecoveryDetails(BurnoutRecovery burnoutRecovery) {
        MaslachBurnoutInventoryDimensions dimensions = burnoutRecovery.getMaslachBurnoutInventoryDimensions();
        System.out.println("Maslach Burnout Inventory dimensions:");
        System.out.printf("  Emotional exhaustion: %.2f%n", dimensions.getEmotionalExhaustion());
        System.out.printf("  Depersonalization: %.2f%n", dimensions.getDepersonalization());
        System.out.printf("  Personal accomplishment: %.2f%n", dimensions.getPersonalAccomplishment());
        printStringList("Recovery strategies", burnoutRecovery.getRecoveryStrategies());
        printStringList("Work-life balance techniques", burnoutRecovery.getWorkLifeBalanceTechniques());
        printStringList("Boundary setting", burnoutRecovery.getBoundarySetting());
    }

    private void printStringList(String heading, List<String> items) {
        System.out.println(heading + ":");

        if (items.isEmpty()) {
            System.out.println("  None available.");
            return;
        }

        for (String item : items) {
            System.out.println("  - " + item);
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
