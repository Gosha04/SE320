package com.SE320.therapy.cli.commands;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;

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
            List<SessionLibraryItemResponse> library = sessionController.getSessionLibrary();

            if (library == null || library.isEmpty()) {
                System.out.println("No CBT sessions are currently available.");
                return;
            }

            for (int i = 0; i < library.size(); i++) {
                SessionLibraryItemResponse item = library.get(i);
                System.out.println((i + 1) + ". " + item.title() + " (Session ID: " + item.sessionId() + ")");
            }
        } catch (Exception e) {
            System.out.println("Unable to load the session library. Please try again.");
        }
    }

    private void startNewSession() {
        UUID userId = userCommands.getCurrentUserId();
        if (userId == null) {
            System.out.println("You must be logged in to start a session.");
            return;
        }

        try {
            List<SessionLibraryItemResponse> library = sessionController.getSessionLibrary();

            if (library == null || library.isEmpty()) {
                System.out.println("No CBT sessions are currently available.");
                return;
            }

            System.out.println("\n--- Start New Session ---");
            for (int i = 0; i < library.size(); i++) {
                SessionLibraryItemResponse item = library.get(i);
                System.out.println((i + 1) + ". " + item.title() + " (Session ID: " + item.sessionId() + ")");
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

            SessionLibraryItemResponse selectedSession = library.get(choice - 1);
            SessionRunResponse session = sessionController.startSession(
                selectedSession.sessionId(),
                new StartSessionRequest(userId, null)
            );

            System.out.println("New CBT session started successfully.");
            System.out.println("Session ID: " + session.sessionId());
            System.out.println("Title: " + session.title());
            System.out.println("Status: " + session.status());
            runInteractiveSession(userId, session, false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to start a new session right now.");
        }
    }

    private void continueSession() {
        UUID userId = userCommands.getCurrentUserId();
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

            SessionRunResponse session = sessionController.continueSession(userId, sessionId);

            System.out.println("Session continued successfully.");
            System.out.println("Session ID: " + session.sessionId());
            System.out.println("Title: " + session.title());
            System.out.println("Status: " + session.status());
            runInteractiveSession(userId, session, true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to continue the session right now.");
        }
    }

    private void endSession() {
        UUID userId = userCommands.getCurrentUserId();
        if (userId == null) {
            System.out.println("You must be logged in to end a session.");
            return;
        }

        try {
            List<SessionRunResponse> history = sessionController.getSessionHistory(userId);

            if (history == null || history.isEmpty()) {
                System.out.println("No session history found.");
                return;
            }

            System.out.println("\n--- Available Sessions ---");
            for (SessionRunResponse session : history) {
                System.out.println("Session ID: " + session.sessionId());
                System.out.println("Title: " + session.title());
                System.out.println("Status: " + session.status());
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

            sessionController.endActiveSession(sessionId, new EndSessionRequest(userId, null));
            System.out.println("Session ended successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to end the session right now.");
        }
    }

    private void viewSessionHistory() {
        UUID userId = userCommands.getCurrentUserId();
        if (userId == null) {
            System.out.println("You must be logged in to view session history.");
            return;
        }

        try {
            System.out.println("\n--- Session History ---");
            List<SessionRunResponse> history = sessionController.getSessionHistory(userId);

            if (history == null || history.isEmpty()) {
                System.out.println("No session history found.");
                return;
            }

            for (SessionRunResponse session : history) {
                System.out.println("Session ID: " + session.sessionId());
                System.out.println("Title: " + session.title());
                System.out.println("Status: " + session.status());
                System.out.println("Started: " + session.startedAt());
                System.out.println("Ended: " + session.endedAt());
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

    private void runInteractiveSession(UUID userId, SessionRunResponse session, boolean resumed) {
        System.out.println();
        System.out.println("--- CBT Session Chat ---");
        System.out.println("Type your message to chat with the assistant.");
        System.out.println("Type pause to return to the session menu without ending the session.");
        System.out.println("Type stop or end to finish the session.");
        if (resumed) {
            System.out.println("Assistant: Welcome back to " + session.title() + ". What would you like to work on next?");
        } else {
            System.out.println("Assistant: Welcome to " + session.title() + ". What would you like to focus on today?");
        }

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                System.out.println("Please enter a message or type pause or end.");
                continue;
            }

            String command = input.toLowerCase(Locale.ROOT);
            if (command.equals("pause") || command.equals("menu") || command.equals("back")) {
                System.out.println("Session paused. You can continue it later from the session menu using its session ID.");
                return;
            }

            if (command.equals("stop") || command.equals("end")) {
                sessionController.endActiveSession(session.sessionId(), new EndSessionRequest(userId, null));
                System.out.println("Session ended successfully.");
                return;
            }

            try {
                SessionChatResponse chatResponse = sessionController.sendChatMessage(
                    session.sessionId(),
                    new SendChatMessageRequest(userId, input, null)
                );
                if (chatResponse != null
                        && chatResponse.assistantMessage() != null
                        && chatResponse.assistantMessage().content() != null
                        && !chatResponse.assistantMessage().content().isBlank()) {
                    System.out.println("Assistant: " + chatResponse.assistantMessage().content());
                } else {
                    System.out.println("Assistant: I am here with you. Tell me a little more.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
                return;
            } catch (Exception e) {
                System.out.println("Unable to send your message right now.");
                return;
            }
        }
    }
}
