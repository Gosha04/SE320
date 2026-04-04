package com.SE320.therapy.cli.commands;

import java.util.Locale;
import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class SessionCommands implements Command {

    private final Scanner scanner;
    private final ViewSessionLibraryCommand viewSessionLibraryCommand;
    private final StartNewSessionCommand startNewSessionCommand;
    private final ContinueSessionCommand continueSessionCommand;
    private final EndSessionCommand endSessionCommand;
    private final ViewSessionHistoryCommand viewSessionHistoryCommand;

    public SessionCommands(Scanner scanner,
                           ViewSessionLibraryCommand viewSessionLibraryCommand,
                           StartNewSessionCommand startNewSessionCommand,
                           ContinueSessionCommand continueSessionCommand,
                           EndSessionCommand endSessionCommand,
                           ViewSessionHistoryCommand viewSessionHistoryCommand) {
        this.scanner = scanner;
        this.viewSessionLibraryCommand = viewSessionLibraryCommand;
        this.startNewSessionCommand = startNewSessionCommand;
        this.continueSessionCommand = continueSessionCommand;
        this.endSessionCommand = endSessionCommand;
        this.viewSessionHistoryCommand = viewSessionHistoryCommand;
    }

    @Override
    public void execute() {
        boolean running = true;

        printMenu();

        while (running) {
            System.out.print("Session command: ");
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "library" -> viewSessionLibraryCommand.execute();
                case "2", "start" -> startNewSessionCommand.execute();
                case "3", "continue" -> continueSessionCommand.execute();
                case "4", "end" -> endSessionCommand.execute();
                case "5", "history" -> viewSessionHistoryCommand.execute();
                case "help" -> printMenu();
                case "6", "back" -> running = false;
                default -> System.out.println("Please choose a valid session option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Session Menu ===");
        System.out.println("1. library");
        System.out.println("2. start");
        System.out.println("3. continue");
        System.out.println("4. end");
        System.out.println("5. history");
        System.out.println("6. back");
        System.out.println("Type a command name, number, or help.");
        System.out.println();
    }
}
