package com.SE320.therapy.cli.commands;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

@Component
public class ContinueSessionCommand implements Command {

    private final SessionController sessionController;
    private final Scanner scanner;
    private final UserCommands userCommands;

    public ContinueSessionCommand(SessionController sessionController, Scanner scanner, UserCommands userCommands) {
        this.sessionController = sessionController;
        this.scanner = scanner;
        this.userCommands = userCommands;
    }

    @Override
    public void execute() {
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

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to continue the session right now.");
        }
    }
}
