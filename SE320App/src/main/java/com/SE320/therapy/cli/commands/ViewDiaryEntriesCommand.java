package com.SE320.therapy.cli.commands;

import com.SE320.therapy.cli.commands.Command;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.service.DiaryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class ViewDiaryEntriesCommand implements Command {

    private final DiaryService diaryService;
    private final Supplier<UUID> currentUserIdSupplier;

    public ViewDiaryEntriesCommand(DiaryService diaryService,
                                   Supplier<UUID> currentUserIdSupplier) {
        this.diaryService = diaryService;
        this.currentUserIdSupplier = currentUserIdSupplier;
    }

    @Override
    public void execute() {
        UUID userId = currentUserIdSupplier.get();

        if (userId == null) {
            System.out.println("You must be logged in to view diary entries.");
            return;
        }

        List<DiaryEntrySummary> entries = diaryService.getEntries(userId);

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
    }
}