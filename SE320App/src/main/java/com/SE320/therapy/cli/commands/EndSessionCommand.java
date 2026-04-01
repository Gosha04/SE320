package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

import java.util.List;
import java.util.Scanner;

public class EndSessionCommand implements Command {

    private final SessionController sessionController;
    private final Scanner scanner;
    private final String userId;

    public EndSessionCommand(SessionController sessionController, Scanner scanner, String userId) {
        this.sessionController = sessionController;
        this.scanner = scanner;
        this.userId = userId;
    }

    @Override
    public void execute() {
        try {
            List<CBTSession> history = sessionController.viewSessionHistory(userId);

            if (history.isEmpty()) {
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
}