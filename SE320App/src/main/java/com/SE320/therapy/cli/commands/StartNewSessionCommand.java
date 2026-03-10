package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

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
            System.out.println("\nEnter session type:");
            String sessionType = scanner.nextLine().trim();

            if (sessionType.isBlank()) {
                System.out.println("Session type cannot be empty.");
                return;
            }

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
