package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

import java.util.Scanner;

public class ContinueSessionCommand implements Command {

    private final SessionController sessionController;
    private final Scanner scanner;
    private final String userId;

    public ContinueSessionCommand(SessionController sessionController, Scanner scanner, String userId) {
        this.sessionController = sessionController;
        this.scanner = scanner;
        this.userId = userId;
    }

    @Override
    public void execute() {
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

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to continue the session right now.");
        }
    }
}