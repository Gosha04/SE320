package com.SE320.therapy.cli.commands;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

@Component
public class SessionCommands implements Command {

    private final SessionController sessionController;
    private final Scanner scanner;
    private final UserCommands userCommands;

    public SessionCommands(SessionController sessionController,
                           Scanner scanner,
                           UserCommands userCommands) {
        this.sessionController = sessionController;
        this.scanner = scanner;
        this.userCommands = userCommands;
    }

    @Override
    public void execute() {
        boolean running = true;

        printMenu();

        while (running) {
            System.out.print("Session command: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "library" -> viewSessionLibrary();
                case "2", "start" -> startNewSession();
                case "3", "continue" -> continueSession();
                case "4", "end" -> endSession();
                case "5", "history" -> viewSessionHistory();
                case "help" -> printMenu();
                case "6", "back" -> running = false;
                default -> System.out.println("Please choose a valid session option.");
            }
        }
    }

    private void viewSessionLibrary() {
        try {
            System.out.println("\n--- Session Library ---");
            List<String> library = sessionController.viewSessionLibrary();

            if (library == null || library.isEmpty()) {
                System.out.println("No CBT sessions are currently available.");
                return;
            }

            for (int i = 0; i < library.size(); i++) {
                System.out.println((i + 1) + ". " + library.get(i));
            }
        } catch (Exception e) {
            System.out.println("Unable to load the session library. Please try again.");
        }
    }

    private void startNewSession() {
        String userId = userCommands.getCurrentUserIdAsString();
        if (userId == null) {
            System.out.println("You must be logged in to start a session.");
            return;
        }

        try {
            List<String> library = sessionController.viewSessionLibrary();

            if (library == null || library.isEmpty()) {
                System.out.println("No CBT sessions are currently available.");
                return;
            }

            System.out.println("\n--- Start New Session ---");
            for (int i = 0; i < library.size(); i++) {
                System.out.println((i + 1) + ". " + library.get(i));
            }

            System.out.print("Choose a session by number: ");
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Please enter a session number.");
                return;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Session choice must be a valid number.");
                return;
            }

            if (choice < 1 || choice > library.size()) {
                System.out.println("Please choose a number from the session library.");
                return;
            }

            String sessionType = library.get(choice - 1);
            CBTSession session = sessionController.startNewSession(userId, sessionType);

            System.out.println("New CBT session started successfully.");
            System.out.println("Session ID: " + session.getSessionId());
            System.out.println("Type: " + session.getSessionType());
            System.out.println("Status: " + session.getStatus());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to start a new session right now.");
        }
    }

    private void continueSession() {
        String userId = userCommands.getCurrentUserIdAsString();
        if (userId == null) {
            System.out.println("You must be logged in to continue a session.");
            return;
        }

        try {
            System.out.print("\nEnter session ID to continue: ");
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Session ID cannot be empty.");
                return;
            }

            Long sessionId;
            try {
                sessionId = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Session ID must be a valid number.");
                return;
            }

            CBTSession session = sessionController.continueSession(userId, sessionId);

            System.out.println("Session continued successfully.");
            System.out.println("Session ID: " + session.getSessionId());
            System.out.println("Type: " + session.getSessionType());
            System.out.println("Status: " + session.getStatus());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to continue the session right now.");
        }
    }

    private void endSession() {
        String userId = userCommands.getCurrentUserIdAsString();
        if (userId == null) {
            System.out.println("You must be logged in to end a session.");
            return;
        }

        try {
            List<CBTSession> history = sessionController.viewSessionHistory(userId);

            if (history == null || history.isEmpty()) {
                System.out.println("No session history found.");
                return;
            }

            System.out.println("\n--- Available Sessions ---");
            for (CBTSession session : history) {
                System.out.println("Session ID: " + session.getSessionId());
                System.out.println("Type: " + session.getSessionType());
                System.out.println("Status: " + session.getStatus());
                System.out.println("-------------------------");
            }

            System.out.print("Enter the session ID to end: ");
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Session ID cannot be empty.");
                return;
            }

            Long sessionId;
            try {
                sessionId = Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Session ID must be a number.");
                return;
            }

            sessionController.endSession(userId, sessionId);
            System.out.println("Session ended successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to end the session right now.");
        }
    }

    private void viewSessionHistory() {
        String userId = userCommands.getCurrentUserIdAsString();
        if (userId == null) {
            System.out.println("You must be logged in to view session history.");
            return;
        }

        try {
            System.out.println("\n--- Session History ---");
            List<CBTSession> history = sessionController.viewSessionHistory(userId);

            if (history == null || history.isEmpty()) {
                System.out.println("No session history found.");
                return;
            }

            for (CBTSession session : history) {
                System.out.println("Session ID: " + session.getSessionId());
                System.out.println("Type: " + session.getSessionType());
                System.out.println("Status: " + session.getStatus());
                System.out.println("Started: " + session.getStartedAt());
                System.out.println("Ended: " + session.getEndedAt());
                System.out.println("-------------------------");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to load session history. Please try again.");
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Session Menu ===");
        System.out.println("1. library");
        System.out.println("2. start");
        System.out.println("3. continue");
        System.out.println("4. end");
        System.out.println("5. history");
        System.out.println("6. back");
        System.out.println("Type a command name, number, or help.");
        System.out.println();
    }
}
