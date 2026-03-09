package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

import java.util.List;

public class ViewSessionHistoryCommand implements Command {

    private final SessionController sessionController;
    private final String userId;

    public ViewSessionHistoryCommand(SessionController sessionController, String userId) {
        this.sessionController = sessionController;
        this.userId = userId;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n--- Session History ---");
            List<CBTSession> history = sessionController.viewSessionHistory(userId);

            if (history.isEmpty()) {
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
}
