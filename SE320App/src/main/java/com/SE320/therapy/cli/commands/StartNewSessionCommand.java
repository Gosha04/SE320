package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

import java.util.List;
import java.util.Scanner;

public class StartNewSessionCommand implements Command {

    private final SessionController sessionController;
    private final Scanner scanner;
    private final String userId;

    public StartNewSessionCommand(SessionController sessionController, Scanner scanner, String userId) {
        this.sessionController = sessionController;
        this.scanner = scanner;
        this.userId = userId;
    }

    @Override
    public void execute() {
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

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to start a new session right now.");
        }
    }
}