package com.SE320.therapy.cli.commands;

import com.SE320.therapy.cli.commands.Command;
import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.service.DiaryService;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class NewDiaryEntryCommand implements Command {

    private final DiaryService diaryService;
    private final Scanner scanner;
    private final Supplier<UUID> currentUserIdSupplier;

    public NewDiaryEntryCommand(DiaryService diaryService,
                                Scanner scanner,
                                Supplier<UUID> currentUserIdSupplier) {
        this.diaryService = diaryService;
        this.scanner = scanner;
        this.currentUserIdSupplier = currentUserIdSupplier;
    }

    @Override
    public void execute() {
        UUID userId = currentUserIdSupplier.get();

        if (userId == null) {
            System.out.println("You must be logged in to create a diary entry.");
            return;
        }

        try {
            System.out.print("Situation: ");
            String situation = scanner.nextLine();

            System.out.print("Automatic thought: ");
            String automaticThought = scanner.nextLine();

            System.out.print("Alternative thought: ");
            String alternativeThought = scanner.nextLine();

            int moodBefore = readMood("Mood before (1-10): ");
            int moodAfter = readMood("Mood after (1-10): ");

            DiaryEntryCreateRequest request = new DiaryEntryCreateRequest(
                    situation,
                    automaticThought,
                    alternativeThought,
                    moodBefore,
                    moodAfter
            );

            diaryService.createEntry(userId, request);
            System.out.println("Diary entry created successfully.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private int readMood(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                int mood = Integer.parseInt(input);
                if (mood >= 1 && mood <= 10) {
                    return mood;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a number from 1 to 10.");
        }
    }
}