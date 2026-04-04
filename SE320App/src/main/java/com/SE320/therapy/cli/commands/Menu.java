package com.SE320.therapy.cli.commands;

import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.SE320.therapy.controller.SessionController;

@Component
public class Menu implements Command {

    private final Scanner scanner;
    private final UserCommands userCommands;
    private final SessionController sessionController;
    private final NewDiaryEntryCommand newDiaryEntryCommand;
    private final ViewDiaryEntriesCommand viewDiaryEntriesCommand;
    private final ViewDiaryInsightsCommand viewDiaryInsightsCommand;
    private final DashboardCommands dashboardCommands;
    private final Supplier<UUID> currentUserIdSupplier;

    public Menu(Scanner scanner,
                UserCommands userCommands,
                SessionController sessionController,
                NewDiaryEntryCommand newDiaryEntryCommand,
                ViewDiaryEntriesCommand viewDiaryEntriesCommand,
                ViewDiaryInsightsCommand viewDiaryInsightsCommand,
                DashboardCommands dashboardCommands,
                Supplier<UUID> currentUserIdSupplier) {
        this.scanner = scanner;
        this.userCommands = userCommands;
        this.sessionController = sessionController;
        this.newDiaryEntryCommand = newDiaryEntryCommand;
        this.viewDiaryEntriesCommand = viewDiaryEntriesCommand;
        this.viewDiaryInsightsCommand = viewDiaryInsightsCommand;
        this.dashboardCommands = dashboardCommands;
        this.currentUserIdSupplier = currentUserIdSupplier;
    }

    @Override
    public void execute() {
        boolean running = true;

        printMainMenu();

        while (running) {
            System.out.print("Select an area: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "authentication", "auth" -> userCommands.execute();
                case "2", "session", "sessions" -> executeSessionMenu();
                case "3", "diary" -> executeDiaryMenu();
                case "4", "dashboard" -> dashboardCommands.execute();
                case "5", "crisis" -> System.out.println("Crisis commands are not available yet.");
                case "6", "settings" -> System.out.println("Settings commands are not available yet.");
                case "help" -> printMainMenu();
                case "7", "exit" -> running = false;
                default -> System.out.println("Please choose a valid menu option.");
            }
        }
    }

    private void executeSessionMenu() {
        boolean running = true;

        printSessionMenu();

        while (running) {
            System.out.print("Session command: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "library" -> new ViewSessionLibraryCommand(sessionController).execute();
                case "2", "start" -> {
                    String userId = getCurrentUserIdAsString();
                    if (userId == null) {
                        System.out.println("You must be logged in to start a session.");
                    } else {
                        new StartNewSessionCommand(sessionController, scanner, userId).execute();
                    }
                }
                case "3", "history" -> {
                    String userId = getCurrentUserIdAsString();
                    if (userId == null) {
                        System.out.println("You must be logged in to view session history.");
                    } else {
                        new ViewSessionHistoryCommand(sessionController, userId).execute();
                    }
                }
                case "help" -> printSessionMenu();
                case "4", "back" -> running = false;
                default -> System.out.println("Please choose a valid session option.");
            }
        }
    }

    private void executeDiaryMenu() {
        boolean running = true;

        printDiaryMenu();

        while (running) {
            System.out.print("Diary command: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "new" -> newDiaryEntryCommand.execute();
                case "2", "entries", "view" -> viewDiaryEntriesCommand.execute();
                case "3", "insights" -> viewDiaryInsightsCommand.execute();
                case "help" -> printDiaryMenu();
                case "4", "back" -> running = false;
                default -> System.out.println("Please choose a valid diary option.");
            }
        }
    }

    private String getCurrentUserIdAsString() {
        UUID currentUserId = currentUserIdSupplier.get();
        return currentUserId != null ? currentUserId.toString() : null;
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("=== Main Menu ===");
        System.out.println("1. Authentication");
        System.out.println("2. Session");
        System.out.println("3. Diary");
        System.out.println("4. Dashboard");
        System.out.println("5. Crisis");
        System.out.println("6. Settings");
        System.out.println("7. Exit");
        System.out.println("Type a menu name, number, or help.");
        System.out.println();
    }

    //
    private void printSessionMenu() {
        System.out.println();
        System.out.println("=== Session Menu ===");
        System.out.println("1. library");
        System.out.println("2. start");
        System.out.println("3. history");
        System.out.println("4. back");
        System.out.println("Type a command name, number, or help.");
        System.out.println();
    }


    // Should be done in diary menu
    private void printDiaryMenu() {
        System.out.println();
        System.out.println("=== Diary Menu ===");
        System.out.println("1. new");
        System.out.println("2. entries");
        System.out.println("3. insights");
        System.out.println("4. back");
        System.out.println("Type a command name, number, or help.");
        System.out.println();
    }
}
