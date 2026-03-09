package com.SE320.therapy.service;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.repository.DiaryEntryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiaryServiceImpl implements DiaryService {
    private final DiaryEntryRepository diaryEntryRepository;
    
    public DiaryServiceImpl(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    @Override
    public void createEntry(UUID userId, DiaryEntryCreateRequest request) {
        validateRequest(request);

        DiaryEntry entry = new DiaryEntry(
                UUID.randomUUID(),
                userId,
                request.getSituation().trim(),
                request.getAutomaticThought().trim(),
                request.getAlternativeThought().trim(),
                request.getMoodBefore(),
                request.getMoodAfter(),
                LocalDateTime.now(),
                false
        );

        diaryEntryRepository.save(entry);
    }

    @Override
    public List<DiaryEntrySummary> getEntries(UUID userId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUserId(userId);
        List<DiaryEntrySummary> summaries = new ArrayList<>();

        for (DiaryEntry entry : entries) {
            String preview = entry.getSituation();
            if (preview.length() > 40) {
                preview = preview.substring(0, 40) + "...";
            }

            summaries.add(new DiaryEntrySummary(
                    entry.getId(),
                    preview,
                    entry.getMoodBefore(),
                    entry.getMoodAfter(),
                    entry.getCreatedAt()
            ));
        }

        return summaries;
    }
    
    @Override
    public DiaryInsights getInsights(UUID userId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUserId(userId);

        if (entries.isEmpty()) {
            return new DiaryInsights(0, 0.0, 0);
        }

        int totalImprovement = 0;
        int bestImprovement = Integer.MIN_VALUE;

        for (DiaryEntry entry : entries) {
            int improvement = entry.getMoodAfter() - entry.getMoodBefore();
            totalImprovement += improvement;

            if (improvement > bestImprovement) {
                bestImprovement = improvement;
            }
        }

        double averageImprovement = (double) totalImprovement / entries.size();

        return new DiaryInsights(entries.size(), averageImprovement, bestImprovement);
    }

    private void validateRequest(DiaryEntryCreateRequest request) {
        if (request.getSituation() == null || request.getSituation().trim().isEmpty()) {
            throw new IllegalArgumentException("Situation cannot be empty.");
        }

        if (request.getAutomaticThought() == null || request.getAutomaticThought().trim().isEmpty()) {
            throw new IllegalArgumentException("Automatic thought cannot be empty.");
        }

        if (request.getAlternativeThought() == null || request.getAlternativeThought().trim().isEmpty()) {
            throw new IllegalArgumentException("Alternative thought cannot be empty.");
        }

        if (request.getMoodBefore() < 1 || request.getMoodBefore() > 10) {
            throw new IllegalArgumentException("Mood before must be between 1 and 10.");
        }

        if (request.getMoodAfter() < 1 || request.getMoodAfter() > 10) {
            throw new IllegalArgumentException("Mood after must be between 1 and 10.");
        }
    }

}

