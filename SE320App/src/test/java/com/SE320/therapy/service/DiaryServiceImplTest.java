package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.UserRepository;

class DiaryServiceImplTest {

    private DiaryEntryRepository diaryEntryRepository;
    private UserRepository userRepository;
    private AiService aiService;
    private DiaryServiceImpl diaryService;

    @BeforeEach
    void setUp() {
        diaryEntryRepository = mock(DiaryEntryRepository.class);
        userRepository = mock(UserRepository.class);
        aiService = mock(AiService.class);
        diaryService = new DiaryServiceImpl(diaryEntryRepository, userRepository, aiService);
    }

    @Test
    void suggestDistortions_appendsReframingPromptToTopSuggestion() {
        when(aiService.analyzeThought(any())).thenReturn(List.of(
                new DistortionSuggestion("catastrophizing", 0.9d, "This jumps to the worst case.")));
        when(aiService.generateReframingPrompts(any(), any())).thenReturn(List.of(
                "What is the most likely outcome, not just the worst one?"));

        List<DistortionSuggestion> suggestions = diaryService.suggestDistortions("Everything is ruined.");

        assertEquals(1, suggestions.size());
        assertEquals(
                "This jumps to the worst case. Reframing prompt: What is the most likely outcome, not just the worst one?",
                suggestions.get(0).getReasoning());
    }

    @Test
    void getInsights_usesAiServiceWhenAvailable() {
        UUID userId = UUID.randomUUID();
        DiaryInsights expected = new DiaryInsights(4, 1.75d, 3);
        when(aiService.generateInsights(userId)).thenReturn(expected);

        DiaryInsights actual = diaryService.getInsights(userId);

        assertEquals(expected.getTotalEntries(), actual.getTotalEntries());
        assertEquals(expected.getAverageMoodImprovement(), actual.getAverageMoodImprovement());
        assertEquals(expected.getBestMoodImprovement(), actual.getBestMoodImprovement());
    }
}
