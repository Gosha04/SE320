package com.SE320.therapy.cli.commands;

import java.util.List;

import org.springframework.stereotype.Component;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;

@Component
public class ViewSessionHistoryCommand implements Command {

    private final SessionController sessionController;
    private final UserCommands userCommands;

    public ViewSessionHistoryCommand(SessionController sessionController, UserCommands userCommands) {
        this.sessionController = sessionController;
        this.userCommands = userCommands;
    }

    @Override
    public void execute() {
        String userId = userCommands.getCurrentUserIdAsString();
        if (userId == null) {
            System.out.println("You must be logged in to view session history.");
            return;
        }

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
