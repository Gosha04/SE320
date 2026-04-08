package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.SE320.therapy.service.rag.RagContextBuilder;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.entity.CognitiveDistortion;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.objects.SeverityLevel;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.CognitiveDistortionRepository;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.UserSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

class OpenAiServiceTest {

    private RagContextBuilder ragContextBuilder;
    private UserSessionRepository userSessionRepository;
    private DiaryEntryRepository diaryEntryRepository;
    private ChatMessageRepository chatMessageRepository;
    private CognitiveDistortionRepository cognitiveDistortionRepository;
    private OpenAiService openAiService;

    @BeforeEach
    void setUp() {
        ragContextBuilder = mock(RagContextBuilder.class);
        userSessionRepository = mock(UserSessionRepository.class);
        diaryEntryRepository = mock(DiaryEntryRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        cognitiveDistortionRepository = mock(CognitiveDistortionRepository.class);

        openAiService = new OpenAiService(
                RestClient.builder(),
                new ObjectMapper(),
                ragContextBuilder,
                userSessionRepository,
                diaryEntryRepository,
                chatMessageRepository,
                cognitiveDistortionRepository,
                "",
                "gpt-4o-mini",
                "https://api.openai.com/v1");
    }

    @Test
    void analyzeThought_withoutApiKey_usesHeuristicFallback() {
        CognitiveDistortion allOrNothing = new CognitiveDistortion();
        allOrNothing.setId("all-or-nothing");
        CognitiveDistortion catastrophizing = new CognitiveDistortion();
        catastrophizing.setId("catastrophizing");
        when(cognitiveDistortionRepository.findAll()).thenReturn(List.of(allOrNothing, catastrophizing));

        List<DistortionSuggestion> suggestions = openAiService.analyzeThought(
                "I always mess things up and this is a total disaster.");

        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.stream().anyMatch(s -> "all-or-nothing".equals(s.getDistortionId())));
        assertTrue(suggestions.stream().anyMatch(s -> "catastrophizing".equals(s.getDistortionId())));
    }

    @Test
    void detectCrisis_withoutApiKey_flagsSevereKeywords() {
        CrisisDetectionResponse response = openAiService.detectCrisis("Sometimes I want to kill myself.");

        assertTrue(response.crisisDetected());
        assertTrue(response.severityLevel() == SeverityLevel.SIGNIFICANT);
        assertFalse(response.recommendedNextSteps().isEmpty());
    }

    @Test
    void generateResponse_withoutApiKey_usesFallbackTherapeuticReply() {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        UserSession session = new UserSession();
        session.setId(sessionId);
        session.setUser(user);

        when(userSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(ragContextBuilder.buildContext(userId, sessionId, "I feel overwhelmed at work"))
                .thenReturn("Recent diary pattern: burnout spikes after meetings.");

        String response = openAiService.generateResponse(sessionId, "I feel overwhelmed at work");

        assertTrue(response.contains("hardest part"));
        assertTrue(response.contains("small next step"));
    }

    @Test
    void parseStringArray_acceptsObjectWrappedPromptArrays() throws Exception {
        Method method = OpenAiService.class.getDeclaredMethod("parseStringArray", String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> prompts = (List<String>) method.invoke(
                openAiService,
                """
                {
                  "prompts": [
                    "What evidence supports this thought?",
                    "What evidence points in a different direction?"
                  ]
                }
                """);

        assertEquals(2, prompts.size());
        assertEquals("What evidence supports this thought?", prompts.get(0));
    }

    @Test
    void parseStringArray_acceptsNumberedListsAsFallback() throws Exception {
        Method method = OpenAiService.class.getDeclaredMethod("parseStringArray", String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> prompts = (List<String>) method.invoke(
                openAiService,
                """
                1. What evidence supports this thought?
                2. What evidence points in a different direction?
                3. What is a more balanced way to describe this situation?
                """);

        assertEquals(3, prompts.size());
        assertEquals("What evidence supports this thought?", prompts.get(0));
    }
}
