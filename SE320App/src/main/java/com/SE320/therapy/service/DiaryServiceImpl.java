package com.SE320.therapy.service;

import com.SE320.therapy.dto.*;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.exception.ApiException;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DiaryServiceImpl implements DiaryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    public DiaryServiceImpl(DiaryEntryRepository diaryEntryRepository,
                            UserRepository userRepository) {
        this(diaryEntryRepository, userRepository, null);
    }

    @Autowired
    public DiaryServiceImpl(DiaryEntryRepository diaryEntryRepository,
                            UserRepository userRepository,
                            AiService aiService) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

    @Override
    public DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND",
                        "No user was found for the provided userId.",
                        List.of(new ApiErrorDetail("userId", "No user exists with the provided id"))
                ));

        DiaryEntry entry = new DiaryEntry(
                UUID.randomUUID(),
                user,
                request.getSituation().trim(),
                request.getAutomaticThought().trim(),
                request.getAlternativeThought().trim(),
                request.getMoodBefore(),
                request.getMoodAfter(),
                null,
                false
        );

        diaryEntryRepository.save(entry);

        return new DiaryEntryResponse(
                entry.getId(),
                "Diary entry created successfully.",
                entry.getCreatedAt()
        );
    }

    @Override
    public Page<DiaryEntrySummary> getEntries(UUID userId, Pageable pageable) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
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

        if (pageable == null || pageable.isUnpaged()) {
        return new PageImpl<>(summaries);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), summaries.size());

        List<DiaryEntrySummary> pageContent =
            start >= summaries.size() ? List.of() : summaries.subList(start, end);

        return new PageImpl<>(pageContent, pageable, summaries.size());
    }

    @Override
    public DiaryEntryDetail getEntryDetail(UUID entryId) {
        DiaryEntry entry = diaryEntryRepository.findByIdAndDeletedFalse(entryId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "DIARY_ENTRY_NOT_FOUND",
                        "No diary entry was found for the provided entryId.",
                        List.of(new ApiErrorDetail("entryId", "No active diary entry exists with the provided id"))
                ));

        return new DiaryEntryDetail(
                entry.getId(),
                entry.getUser().getId(),
                entry.getSituation(),
                entry.getAutomaticThought(),
                entry.getAlternativeThought(),
                entry.getMoodBefore(),
                entry.getMoodAfter(),
                entry.getCreatedAt()
        );
    }

    @Override
    public void deleteEntry(UUID entryId) {
        DiaryEntry entry = diaryEntryRepository.findByIdAndDeletedFalse(entryId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "DIARY_ENTRY_NOT_FOUND",
                        "No diary entry was found for the provided entryId.",
                        List.of(new ApiErrorDetail("entryId", "No active diary entry exists with the provided id"))
                ));

        entry.setDeleted(true);
        diaryEntryRepository.save(entry);
    }

    @Override
    public DiaryInsights getInsights(UUID userId) {
        if (aiService != null) {
            return aiService.generateInsights(userId);
        }

        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);

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

    @Override
    public List<DistortionSuggestion> suggestDistortions(String thought) {
        if (thought == null || thought.trim().isEmpty()) {
            throw new IllegalArgumentException("Thought cannot be empty.");
        }

        if (aiService != null) {
            List<DistortionSuggestion> suggestions = new ArrayList<>(aiService.analyzeThought(thought));
            if (!suggestions.isEmpty()) {
                List<String> distortionIds = suggestions.stream()
                        .map(DistortionSuggestion::getDistortionId)
                        .toList();
                List<String> reframingPrompts = aiService.generateReframingPrompts(thought, distortionIds);
                if (!reframingPrompts.isEmpty()) {
                    DistortionSuggestion firstSuggestion = suggestions.get(0);
                    String reasoning = firstSuggestion.getReasoning() == null ? "" : firstSuggestion.getReasoning().trim();
                    String updatedReasoning = reasoning.isEmpty()
                            ? "Reframing prompt: " + reframingPrompts.get(0)
                            : reasoning + " Reframing prompt: " + reframingPrompts.get(0);
                    firstSuggestion.setReasoning(updatedReasoning);
                }
            }
            return suggestions;
        }

        List<DistortionSuggestion> suggestions = new ArrayList<>();

        String lowerThought = thought.toLowerCase();

        if (lowerThought.contains("always") || lowerThought.contains("never")) {
            suggestions.add(new DistortionSuggestion(
                    "all-or-nothing",
                    0.9,
                    "Uses absolute words like always or never."
            ));
        }

        if (lowerThought.contains("worst") || lowerThought.contains("ruined") || lowerThought.contains("disaster")) {
            suggestions.add(new DistortionSuggestion(
                    "catastrophizing",
                    0.85,
                    "Assumes the worst possible outcome."
            ));
        }

        if (lowerThought.contains("they think") || lowerThought.contains("they hate me")) {
            suggestions.add(new DistortionSuggestion(
                    "mind-reading",
                    0.8,
                    "Assumes what other people are thinking without proof."
            ));
        }

        return suggestions;
    }
}
