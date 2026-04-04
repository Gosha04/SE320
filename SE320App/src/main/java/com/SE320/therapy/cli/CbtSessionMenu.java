package com.SE320.therapy.cli;

import com.SE320.therapy.cli.commands.Command;
import com.SE320.therapy.cli.commands.ContinueSessionCommand;
import com.SE320.therapy.cli.commands.EndSessionCommand;
import com.SE320.therapy.cli.commands.StartNewSessionCommand;
import com.SE320.therapy.cli.commands.UserCommands;
import com.SE320.therapy.cli.commands.ViewSessionHistoryCommand;
import com.SE320.therapy.cli.commands.ViewSessionLibraryCommand;
import com.SE320.therapy.controller.SessionController;

import java.util.HashMap;
import java.util.Map;
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
        boolean running = true;

        while (running) {
            System.out.println("\n--- CBT Sessions ---");
            System.out.println("1. View Session Library");
            System.out.println("2. Start New Session");
            System.out.println("3. Continue Session");
            System.out.println("4. End Session");
            System.out.println("5. View Session History");
            System.out.println("6. Back");

            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();

            UserCommands fixedUserCommands = new FixedUserCommands(userId);
            Map<String, Command> commands = new HashMap<>();
            commands.put("1", new ViewSessionLibraryCommand(sessionController));
            commands.put("2", new StartNewSessionCommand(sessionController, scanner, fixedUserCommands));
            commands.put("3", new ContinueSessionCommand(sessionController, scanner, fixedUserCommands));
            commands.put("4", new EndSessionCommand(sessionController, scanner, fixedUserCommands));
            commands.put("5", new ViewSessionHistoryCommand(sessionController, fixedUserCommands));

            if (input.equals("6")) {
                running = false;
                continue;
            }

            Command command = commands.get(input);

            if (command == null) {
                System.out.println("Invalid choice. Please enter 1, 2, 3, 4, 5, or 6.");
                continue;
            }

            command.execute();
        }
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
