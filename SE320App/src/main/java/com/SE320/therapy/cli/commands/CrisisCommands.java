package com.SE320.therapy.cli.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.controller.CrisisController;
import com.SE320.therapy.dto.CrisisDetectionRequest;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.objects.Crisis;
import com.SE320.therapy.objects.UserType;

@Component
public class CrisisCommands implements Command {
    private final CrisisController crisisController;
    private final UserCommands userCommands;
    private final Scanner scanner;

    public CrisisCommands(CrisisController crisisController,
                          UserCommands userCommands,
                          Scanner scanner) {
        this.crisisController = crisisController;
        this.userCommands = userCommands;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        boolean running = true;

        printMenu();

        while (running) {
            System.out.print("Crisis command: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "hub", "view" -> handleViewCrisisHub();
                case "2", "coping", "strategies" -> handleGetCopingStrategies();
                case "3", "safety", "plan" -> handleGetSafetyPlan();
                case "4", "detect" -> handleDetectCrisisIndicators();
                case "5", "update" -> handleUpdateSafetyPlan();
                case "help" -> printMenu();
                case "6", "back" -> running = false;
                default -> System.out.println("Please choose a valid crisis option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Crisis Menu ===");
        System.out.println("1. hub");
        System.out.println("2. coping");
        System.out.println("3. safety");
        System.out.println("4. detect");
        System.out.println("5. update");
        System.out.println("6. back");
        System.out.println("Patients can only access their own crisis hub and safety plan.");
        System.out.println("Doctors can view any patient's crisis hub and update any safety plan.");
        System.out.println("Admins cannot access user-specific crisis information.");
        System.out.println("Type a command name, number, or help.");
        System.out.println();
    }

    private void handleViewCrisisHub() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (currentUserId == null || currentUserType == null) {
            System.out.println("You must be logged in to access crisis commands.");
            return;
        }

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to access crisis patient information.");
            return;
        }

        try {
            UUID userId = resolveCrisisUserId(currentUserId, currentUserType, "Patient ID for crisis hub: ");
            if (userId == null) {
                return;
            }

            Crisis crisis = crisisController.getCrisisHub(userId);
            if (!crisis.canBeViewedBy(currentUserId, currentUserType)) {
                System.out.println("You are not allowed to view that crisis hub.");
                return;
            }

            printCrisisHub(crisis, crisis.getOwnerUserId(), crisis.isOwnedBy(currentUserId));
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load crisis hub: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load crisis hub right now.");
        }
    }

    private void handleGetCopingStrategies() {
        try {
            List<String> copingStrategies = crisisController.getCopingStrategies();
            System.out.println();
            printStringList("Coping Strategies", copingStrategies);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load coping strategies: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load coping strategies right now.");
        }
    }

    private void handleGetSafetyPlan() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (currentUserId == null || currentUserType == null) {
            System.out.println("You must be logged in to access crisis commands.");
            return;
        }

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to access crisis patient information.");
            return;
        }

        try {
            UUID userId = resolveCrisisUserId(currentUserId, currentUserType, "Patient ID for safety plan: ");
            if (userId == null) {
                return;
            }

            List<String> safetyPlan = crisisController.getSafetyPlan(userId);
            System.out.println();
            printStringList("Safety Plan for " + userId, safetyPlan);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to load safety plan: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load safety plan right now.");
        }
    }

    private void handleDetectCrisisIndicators() {
        try {
            System.out.print("Message to analyze: ");
            String message = scanner.nextLine().trim();
            List<String> indicators = readStringList(
                "Observed indicator",
                "Enter observed indicators one per line. Leave blank when finished."
            );

            CrisisDetectionRequest request = new CrisisDetectionRequest(message, indicators);
            CrisisDetectionResponse response = crisisController.detectCrisisIndicators(request);
            printDetectionResponse(response);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to detect crisis indicators: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to detect crisis indicators right now.");
        }
    }

    private void handleUpdateSafetyPlan() {
        UUID currentUserId = userCommands.getCurrentUserId();
        UserType currentUserType = userCommands.getCurrentUserType();

        if (currentUserId == null || currentUserType == null) {
            System.out.println("You must be logged in to access crisis commands.");
            return;
        }

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to access crisis patient information.");
            return;
        }

        try {
            UUID userId = resolveCrisisUserId(currentUserId, currentUserType, "Patient ID for safety plan update: ");
            if (userId == null) {
                return;
            }

            List<String> steps = readStringList(
                "Safety plan step",
                "Enter updated safety plan steps one per line. Leave blank when finished."
            );

            List<String> updatedSafetyPlan = crisisController.updateSafetyPlan(userId, steps);
            System.out.println("Safety plan updated successfully.");
            printStringList("Updated Safety Plan", updatedSafetyPlan);
        } catch (IllegalArgumentException | ResponseStatusException e) {
            System.out.println("Unable to update safety plan: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to update safety plan right now.");
        }
    }

    private UUID resolveCrisisUserId(UUID currentUserId, UserType currentUserType, String doctorPrompt) {
        if (currentUserType == UserType.PATIENT) {
            return currentUserId;
        }

        if (currentUserType == UserType.ADMIN) {
            System.out.println("Admins are not allowed to access crisis patient information.");
            return null;
        }

        while (true) {
            System.out.print(doctorPrompt);
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

    private List<String> readStringList(String itemLabel, String instructions) {
        System.out.println(instructions);
        List<String> items = new ArrayList<>();

        while (true) {
            System.out.print(itemLabel + ": ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return items;
            }

            items.add(input);
        }
    }

    private void printCrisisHub(Crisis crisis, UUID userId, boolean ownHub) {
        System.out.println();
        System.out.println(ownHub ? "=== Your Crisis Hub ===" : "=== Crisis Hub for " + userId + " ===");
        printStringList("Warning Sign Recognition", crisis.getWarningSignRecognition());
        printStringList("De-escalation Techniques", crisis.getDeescalationTechniques());
        printStringList("Safety Planning Steps", crisis.getSafetyPlanningSteps());
        printStringList("Emergency Resources", crisis.getEmergencyResources());
    }

    private void printDetectionResponse(CrisisDetectionResponse response) {
        System.out.println();
        System.out.println("=== Crisis Detection Result ===");
        System.out.println("Crisis detected: " + response.crisisDetected());
        System.out.println("Severity level: " + response.severityLevel());
        printStringList("Matched Indicators", response.matchedIndicators());
        printStringList("Recommended Next Steps", response.recommendedNextSteps());
    }

    private void printStringList(String heading, List<String> items) {
        System.out.println(heading + ":");

        if (items == null || items.isEmpty()) {
            System.out.println("  None available.");
            return;
        }

        for (String item : items) {
            System.out.println("  - " + item);
        }
    }
}
