package com.SE320.therapy.cli.commands;

import java.util.Locale;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class Menu implements Command {

    private final Scanner scanner;
    private final UserCommands userCommands;
    private final SessionCommands sessionCommands;
    private final DiaryCommands diaryCommands;
    private final DashboardCommands dashboardCommands;  
    private final CrisisCommands crisisCommands;
    private final SettingsCommands settingsCommands;
    
    public Menu(Scanner scanner,
                UserCommands userCommands,
                SessionCommands sessionCommands,
                DiaryCommands diaryCommands,
                DashboardCommands dashboardCommands,
                CrisisCommands crisisCommands,
                SettingsCommands settingsCommands) {
        this.scanner = scanner;
        this.userCommands = userCommands;
        this.sessionCommands = sessionCommands;
        this.diaryCommands = diaryCommands;
        this.dashboardCommands = dashboardCommands;
        this.crisisCommands = crisisCommands;
        this.settingsCommands = settingsCommands;
    }

    @Override
    public void execute() {
        boolean running = true;

        printMainMenu();

        while (running) {
            System.out.print("\nSelect an area: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "authentication", "auth" -> userCommands.execute();
                case "2", "session", "sessions" -> sessionCommands.execute();
                case "3", "diary" -> diaryCommands.execute();
                case "4", "dashboard" -> dashboardCommands.execute();
                case "5", "crisis" -> crisisCommands.execute();
                case "6", "settings" -> settingsCommands.execute();
                case "help" -> printMainMenu();
                case "7", "exit" -> running = false;
                default -> System.out.println("Please choose a valid menu option.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Authentication");
        System.out.println("2. Session");
        System.out.println("3. Diary");
        System.out.println("4. Dashboard");
        System.out.println("5. Crisis");
        System.out.println("6. Settings");
        System.out.println("7. Exit");
        System.out.println("Type a menu name, number, or help.\n");
    }
}
