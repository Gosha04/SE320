package com.SE320.therapy.cli.commands;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.service.DiaryService;

@Component
public class DiaryCommands implements Command {

    private final Scanner scanner;
    private final DiaryService diaryService;
    private final Supplier<UUID> currentUserIdSupplier;

    public DiaryCommands(Scanner scanner,
                         DiaryService diaryService,
                         Supplier<UUID> currentUserIdSupplier) {
        this.scanner = scanner;
        this.diaryService = diaryService;
        this.currentUserIdSupplier = currentUserIdSupplier;
    }

    @Override
    public void execute() {
        boolean running = true;

        printDiaryMenu();

        while (running) {
            System.out.print("Diary command: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return;
            }
            String choice = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            switch (choice) {
                case "1", "new" -> createDiaryEntry();
                case "2", "entries", "view" -> viewDiaryEntries();
                case "3", "insights" -> viewDiaryInsights();
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

    private void createDiaryEntry() {
        UUID userId = currentUserIdSupplier.get();
        if (userId == null) {
            System.out.println("You must be logged in to create a diary entry.");
            return;
        }

        try {
            System.out.print("Situation: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return;
            }
            String situation = scanner.nextLine();

            System.out.print("Automatic thought: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return;
            }
            String automaticThought = scanner.nextLine();

            List<DistortionSuggestion> suggestions =
                    diaryService.suggestDistortions(automaticThought);

            if (!suggestions.isEmpty()) {
                System.out.println("\nPossible distortions:");
                for (DistortionSuggestion suggestion : suggestions) {
                     System.out.println("- " + suggestion.getDistortionId() + " (" + String.format("%.2f", suggestion.getConfidence()) + "): " + suggestion.getReasoning());
                }
                System.out.println();
            }

            System.out.print("Alternative thought: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return;
            }
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

    private void viewDiaryEntries() {
        UUID userId = currentUserIdSupplier.get();
        if (userId == null) {
            System.out.println("You must be logged in to view diary entries.");
            return;
        }

        Page<DiaryEntrySummary> page = diaryService.getEntries(userId, Pageable.unpaged());
        List<DiaryEntrySummary> entries = page.getContent();

        if (entries.isEmpty()) {
            System.out.println("No diary entries found.");
            return;
        }

        System.out.println("\nYour Diary Entries:");
        for (int i = 0; i < entries.size(); i++) {
            DiaryEntrySummary entry = entries.get(i);
            System.out.println((i + 1) + ". " + entry.getSituationPreview());
            System.out.println("   Mood: " + entry.getMoodBefore() + " -> " + entry.getMoodAfter());
            System.out.println("   Created: " + entry.getCreatedAt());
        }

        while (true) {
            System.out.print("\nEnter an entry number to view full details, or press Enter to go back: ");
            if (!scanner.hasNextLine()) {
                System.out.println();
                return;
            }
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return;
            }

            try {
                int choice = Integer.parseInt(input);

                if (choice < 1 || choice > entries.size()) {
                    System.out.println("Please choose a valid entry number.");
                    continue;
                }

                UUID entryId = entries.get(choice - 1).getId();
                DiaryEntryDetail detail = diaryService.getEntryDetail(entryId);

                System.out.println("\nDiary Entry Details");
                System.out.println("Situation: " + detail.situation());
                System.out.println("Automatic thought: " + detail.automaticThought());
                System.out.println("Alternative thought: " + detail.alternativeThought());
                System.out.println("Mood: " + detail.moodBefore() + " -> " + detail.moodAfter());
                System.out.println("Created: " + detail.createdAt());
                return;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }
    }

    private void viewDiaryInsights() {
        UUID userId = currentUserIdSupplier.get();
        if (userId == null) {
            System.out.println("You must be logged in to view insights.");
            return;
        }

        DiaryInsights insights = diaryService.getInsights(userId);

        System.out.println("\nDiary Insights");
        System.out.println("Total entries: " + insights.getTotalEntries());
        System.out.printf("Average mood improvement: %.2f%n", insights.getAverageMoodImprovement());
        System.out.println("Best mood improvement: " + insights.getBestMoodImprovement());
    }

    private int readMood(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                System.out.println();
                throw new IllegalArgumentException("Input ended before a mood value was provided.");
            }
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
