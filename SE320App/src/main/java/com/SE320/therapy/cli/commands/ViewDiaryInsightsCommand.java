package com.SE320.therapy.cli.commands;

import com.SE320.therapy.cli.commands.Command;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.service.DiaryService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class ViewDiaryInsightsCommand implements Command {

    private final DiaryService diaryService;
    private final Supplier<UUID> currentUserIdSupplier;

    public ViewDiaryInsightsCommand(DiaryService diaryService,
                                    Supplier<UUID> currentUserIdSupplier) {
        this.diaryService = diaryService;
        this.currentUserIdSupplier = currentUserIdSupplier;
    }

    @Override
    public void execute() {
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
}