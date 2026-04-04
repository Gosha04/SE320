package com.SE320.therapy.cli;

import com.SE320.therapy.cli.commands.SessionCommands;
import com.SE320.therapy.cli.commands.UserCommands;
import com.SE320.therapy.controller.SessionController;

import java.util.Scanner;
import java.util.UUID;

public class CbtSessionMenu {

    private final SessionController sessionController;
    private final Scanner scanner;
    private final String userId;

    public CbtSessionMenu(SessionController sessionController, Scanner scanner, String userId) {
        this.sessionController = sessionController;
        this.scanner = scanner;
        this.userId = userId;
    }

    public void display() {
        UserCommands fixedUserCommands = new FixedUserCommands(userId);
        new SessionCommands(sessionController, scanner, fixedUserCommands).execute();
    }

    private static final class FixedUserCommands extends UserCommands {
        private final String userId;

        private FixedUserCommands(String userId) {
            super(null, new Scanner(new java.io.ByteArrayInputStream(new byte[0])));
            this.userId = userId;
        }

        @Override
        public UUID getCurrentUserId() {
            return userId != null ? UUID.fromString("11111111-1111-1111-1111-111111111111") : null;
        }

        @Override
        public String getCurrentUserIdAsString() {
            return userId;
        }
    }
}
