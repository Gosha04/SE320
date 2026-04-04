package com.SE320.therapy.cli.commands;

import java.util.Locale;
import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class DiaryCommands implements Command {

    private final Scanner scanner;
    private final NewDiaryEntryCommand newDiaryEntryCommand;
    private final ViewDiaryEntriesCommand viewDiaryEntriesCommand;
    private final ViewDiaryInsightsCommand viewDiaryInsightsCommand;

    public DiaryCommands(Scanner scanner,
                         NewDiaryEntryCommand newDiaryEntryCommand,
                         ViewDiaryEntriesCommand viewDiaryEntriesCommand,
                         ViewDiaryInsightsCommand viewDiaryInsightsCommand) {
        this.scanner = scanner;
        this.newDiaryEntryCommand = newDiaryEntryCommand;
        this.viewDiaryEntriesCommand = viewDiaryEntriesCommand;
        this.viewDiaryInsightsCommand = viewDiaryInsightsCommand;
    }

    @Override
    public void execute() {
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