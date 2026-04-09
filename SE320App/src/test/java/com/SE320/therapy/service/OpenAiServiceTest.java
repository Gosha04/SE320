package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.SE320.therapy.service.rag.RagContextBuilder;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.objects.ChatRole;
import com.SE320.therapy.dto.objects.SeverityLevel;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.CognitiveDistortion;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
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

    @Test
    void generateReframingPrompts_withoutApiKey_usesFallbackPromptsAndIncludesDistortionIds() {
        List<String> prompts = openAiService.generateReframingPrompts(
                "I always fail at everything",
                List.of("all-or-nothing", "catastrophizing"));

        assertEquals(3, prompts.size());
        assertTrue(prompts.get(2).contains("all-or-nothing, catastrophizing"));
    }

    @Test
    void analyzeThought_withApiKey_fallsBackToHeuristicsWhenOpenAiCallFails() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        CognitiveDistortion allOrNothing = new CognitiveDistortion();
        allOrNothing.setId("all-or-nothing");
        CognitiveDistortion catastrophizing = new CognitiveDistortion();
        catastrophizing.setId("catastrophizing");
        when(cognitiveDistortionRepository.findAll()).thenReturn(List.of(allOrNothing, catastrophizing));

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"choices\":[{\"message\":{\"content\":\"   \"}}]}", MediaType.APPLICATION_JSON));

        List<DistortionSuggestion> suggestions = configuredService.analyzeThought(
                "I always mess up and this is a disaster.");

        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.stream().anyMatch(s -> "all-or-nothing".equals(s.getDistortionId())));
        assertTrue(suggestions.stream().anyMatch(s -> "catastrophizing".equals(s.getDistortionId())));
        server.verify();
    }

    @Test
    void generateReframingPrompts_withApiKey_fallsBackWhenOpenAiResponseCannotBeParsed() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        """
                        {"choices":[{"message":{"content":"{\\"foo\\":\\"bar\\"}"}}]}
                        """,
                        MediaType.APPLICATION_JSON));

        List<String> prompts = configuredService.generateReframingPrompts(
                "I keep failing at this.",
                List.of("all-or-nothing"));

        assertEquals(3, prompts.size());
        assertTrue(prompts.get(2).contains("all-or-nothing"));
        server.verify();
    }

    @Test
    void detectCrisis_withApiKey_fallsBackToKeywordOnlyWhenOpenAiResponseCannotBeParsed() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        """
                        {"choices":[{"message":{"content":"not-json"}}]}
                        """,
                        MediaType.APPLICATION_JSON));

        CrisisDetectionResponse response = configuredService.detectCrisis("Sometimes I want to kill myself.");

        assertTrue(response.crisisDetected());
        assertEquals(SeverityLevel.SIGNIFICANT, response.severityLevel());
        assertTrue(response.matchedIndicators().stream().anyMatch(i -> i.contains("kill myself")));
        assertTrue(response.recommendedNextSteps().stream().anyMatch(step -> step.contains("988")));
        server.verify();
    }

    @Test
    void generateResponse_prioritizesCrisisSafetyResponseWhenSevereSignalsPresent() {
        String response = openAiService.generateResponse(
                UUID.randomUUID(),
                "I want to kill myself.");

        assertTrue(response.contains("Your safety matters most right now"));
        assertTrue(response.contains("988"));
    }

    @Test
    void generateInsights_returnsCalculatedAggregateValues() {
        UUID userId = UUID.randomUUID();
        DiaryEntry first = diaryEntry(3, 6);
        DiaryEntry second = diaryEntry(7, 8);
        DiaryEntry third = diaryEntry(8, 6);
        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId)).thenReturn(List.of(first, second, third));

        DiaryInsights insights = openAiService.generateInsights(userId);

        assertEquals(3, insights.getTotalEntries());
        assertEquals(0.67d, insights.getAverageMoodImprovement(), 0.01d);
        assertEquals(3, insights.getBestMoodImprovement());
    }

    @Test
    void summarizeSession_withoutMessages_returnsNoTranscriptFallback() {
        UUID sessionId = UUID.randomUUID();
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId)).thenReturn(List.of());

        String summary = openAiService.summarizeSession(sessionId);

        assertEquals("Session completed. No transcript was available for summarization.", summary);
    }

    @Test
    void summarizeSession_withNullSessionId_returnsNoTranscriptFallback() {
        String summary = openAiService.summarizeSession(null);
        assertEquals("Session completed. No transcript was available for summarization.", summary);
    }

    @Test
    void summarizeSession_withOnlyNullOrBlankContents_returnsNoTranscriptFallback() {
        UUID sessionId = UUID.randomUUID();
        ChatMessage nullContent = chatMessage(ChatRole.USER, null);
        ChatMessage blankContent = chatMessage(ChatRole.ASSISTANT, "   ");
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId))
                .thenReturn(Arrays.asList(null, nullContent, blankContent));

        String summary = openAiService.summarizeSession(sessionId);

        assertEquals("Session completed. No transcript was available for summarization.", summary);
    }

    @Test
    void summarizeSession_withoutApiKey_usesLocalTranscriptSummary() {
        UUID sessionId = UUID.randomUUID();
        ChatMessage userMessage = chatMessage(ChatRole.USER, "I feel overwhelmed by deadlines.");
        ChatMessage assistantMessage = chatMessage(ChatRole.ASSISTANT, "Let's break this into one small step.");
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId))
                .thenReturn(List.of(userMessage, assistantMessage));

        String summary = openAiService.summarizeSession(sessionId);

        assertTrue(summary.contains("USER: I feel overwhelmed by deadlines."));
        assertTrue(summary.contains("strongest automatic thought"));
    }

    @Test
    void summarizeSession_withNullRole_usesUnknownRoleInFallbackSummary() {
        UUID sessionId = UUID.randomUUID();
        ChatMessage message = new ChatMessage();
        message.setContent("Need help planning tomorrow.");
        message.setTimestamp(LocalDateTime.now());
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId))
                .thenReturn(List.of(message));

        String summary = openAiService.summarizeSession(sessionId);

        assertTrue(summary.contains("UNKNOWN: Need help planning tomorrow."));
    }

    @Test
    void summarizeSession_withApiKey_usesOpenAiResponseFromCallForText() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = new OpenAiService(
                builder,
                new ObjectMapper(),
                ragContextBuilder,
                userSessionRepository,
                diaryEntryRepository,
                chatMessageRepository,
                cognitiveDistortionRepository,
                "test-key",
                "gpt-4o-mini",
                "https://api.openai.com/v1");

        UUID sessionId = UUID.randomUUID();
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId))
                .thenReturn(List.of(chatMessage(ChatRole.USER, "I feel stuck at work.")));

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andRespond(withSuccess(
                        """
                        {"choices":[{"message":{"content":"  Here is your summary.  "}}]}
                        """,
                        MediaType.APPLICATION_JSON));

        String summary = configuredService.summarizeSession(sessionId);

        assertEquals("Here is your summary.", summary);
        server.verify();
    }

    @Test
    void callForText_returnsTrimmedMessageContent() throws Exception {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andRespond(withSuccess(
                        """
                        {"choices":[{"message":{"content":"  Final answer  "}}]}
                        """,
                        MediaType.APPLICATION_JSON));

        String response = (String) invokePrivate(
                configuredService,
                "callForText",
                new Class<?>[] { String.class, String.class },
                "system",
                "user");

        assertEquals("Final answer", response);
        server.verify();
    }

    @Test
    void callForText_fallsBackToDirectTextField() throws Exception {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withSuccess(
                        """
                        {"choices":[{"text":"  Text fallback  "}]}
                        """,
                        MediaType.APPLICATION_JSON));

        String response = (String) invokePrivate(
                configuredService,
                "callForText",
                new Class<?>[] { String.class, String.class },
                "system",
                "user");

        assertEquals("Text fallback", response);
        server.verify();
    }

    @Test
    void callForText_throwsWhenResponseBodyIsEmpty() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> invokePrivate(
                        configuredService,
                        "callForText",
                        new Class<?>[] { String.class, String.class },
                        "system",
                        "user"));

        assertTrue(ex.getMessage().contains("response body was empty"));
        server.verify();
    }

    @Test
    void callForText_throwsWhenJsonIsMalformed() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withSuccess("{bad-json}", MediaType.APPLICATION_JSON));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> invokePrivate(
                        configuredService,
                        "callForText",
                        new Class<?>[] { String.class, String.class },
                        "system",
                        "user"));

        assertTrue(ex.getMessage().contains("Unable to parse OpenAI response body"));
        server.verify();
    }

    @Test
    void callForText_throwsWhenChoicesAreMissing() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withSuccess("{\"id\":\"abc\"}", MediaType.APPLICATION_JSON));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> invokePrivate(
                        configuredService,
                        "callForText",
                        new Class<?>[] { String.class, String.class },
                        "system",
                        "user"));

        assertTrue(ex.getMessage().contains("did not contain any choices"));
        server.verify();
    }

    @Test
    void callForText_throwsWhenContentAndTextAreEmpty() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        OpenAiService configuredService = createConfiguredService(builder);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withSuccess(
                        """
                        {"choices":[{"message":{"content":"  "},"text":"  "}]}
                        """,
                        MediaType.APPLICATION_JSON));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> invokePrivate(
                        configuredService,
                        "callForText",
                        new Class<?>[] { String.class, String.class },
                        "system",
                        "user"));

        assertTrue(ex.getMessage().contains("content was empty"));
        server.verify();
    }

    @Test
    void parseDistortionSuggestions_parsesObjectWrapperAndFiltersAndSorts() throws Exception {
        @SuppressWarnings("unchecked")
        List<DistortionSuggestion> suggestions = (List<DistortionSuggestion>) invokePrivate(
                openAiService,
                "parseDistortionSuggestions",
                new Class<?>[] { String.class, List.class },
                """
                {
                  "suggestions": [
                    {"distortionId":"catastrophizing","confidence":0.60,"reasoning":"A"},
                    {"distortionId":"mind-reading","confidence":0.92,"reasoning":"B"},
                    {"distortionId":null,"confidence":0.99,"reasoning":"C"}
                  ]
                }
                """,
                List.of("catastrophizing", "mind-reading"));

        assertEquals(2, suggestions.size());
        assertEquals("mind-reading", suggestions.get(0).getDistortionId());
        assertEquals("catastrophizing", suggestions.get(1).getDistortionId());
        assertTrue(suggestions.stream().allMatch(s -> s.getDistortionId() != null));
        assertEquals(
                suggestions.stream()
                        .sorted(Comparator.comparingDouble(DistortionSuggestion::getConfidence).reversed())
                        .toList(),
                suggestions);
    }

    @Test
    void parseDistortionSuggestions_throwsOnMalformedJson() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> invokePrivate(
                        openAiService,
                        "parseDistortionSuggestions",
                        new Class<?>[] { String.class, List.class },
                        "{not-json}",
                        List.of("catastrophizing")));

        assertTrue(ex.getMessage().contains("Unable to parse distortion suggestions"));
    }

    @Test
    void parseStringArray_acceptsItemsAndReframingPromptsKeys() throws Exception {
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) invokePrivate(
                openAiService,
                "parseStringArray",
                new Class<?>[] { String.class },
                """
                {"items":["A","B"]}
                """);

        @SuppressWarnings("unchecked")
        List<String> reframingPrompts = (List<String>) invokePrivate(
                openAiService,
                "parseStringArray",
                new Class<?>[] { String.class },
                """
                {"reframingPrompts":["X","Y","Z"]}
                """);

        assertEquals(List.of("A", "B"), items);
        assertEquals(List.of("X", "Y", "Z"), reframingPrompts);
    }

    @Test
    void parseStringArray_throwsWhenNoSupportedArrayExists() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> invokePrivate(
                        openAiService,
                        "parseStringArray",
                        new Class<?>[] { String.class },
                        "{\"foo\":\"bar\"}"));

        assertTrue(ex.getMessage().contains("Response JSON did not contain a prompt array"));
    }

    @Test
    void extractJson_returnsJsonInsideCodeFence() throws Exception {
        String extracted = (String) invokePrivate(
                openAiService,
                "extractJson",
                new Class<?>[] { String.class },
                """
                ```json
                {"prompts":["one","two"]}
                ```
                """);

        assertEquals("{\"prompts\":[\"one\",\"two\"]}", extracted);
    }

    @Test
    void parseLineList_normalizesBulletAndNumberPrefixes() throws Exception {
        @SuppressWarnings("unchecked")
        List<String> lines = (List<String>) invokePrivate(
                openAiService,
                "parseLineList",
                new Class<?>[] { String.class },
                """
                - First prompt
                2. Second prompt
                • Third prompt

                """);

        assertEquals(List.of("First prompt", "Second prompt", "Third prompt"), lines);
    }

    @Test
    void isConfigured_reflectsApiKeyPresence() throws Exception {
        OpenAiService configured = new OpenAiService(
                RestClient.builder(),
                new ObjectMapper(),
                ragContextBuilder,
                userSessionRepository,
                diaryEntryRepository,
                chatMessageRepository,
                cognitiveDistortionRepository,
                "non-empty-key",
                "gpt-4o-mini",
                "https://api.openai.com/v1");

        boolean configuredResult = (boolean) invokePrivate(configured, "isConfigured", new Class<?>[] {});
        boolean defaultResult = (boolean) invokePrivate(openAiService, "isConfigured", new Class<?>[] {});

        assertTrue(configuredResult);
        assertFalse(defaultResult);
    }

    @Test
    void fallbackTherapeuticResponse_includesContextLeadWhenContextProvided() throws Exception {
        String response = (String) invokePrivate(
                openAiService,
                "fallbackTherapeuticResponse",
                new Class<?>[] { String.class, String.class },
                "I am overloaded",
                "recent context");

        assertTrue(response.contains("I’m keeping your recent diary and session context in mind"));
        assertTrue(response.contains("I am overloaded"));
    }

    @Test
    void heuristicDistortionSuggestions_detectsMultiplePatternsSortedByConfidence() throws Exception {
        @SuppressWarnings("unchecked")
        List<DistortionSuggestion> suggestions = (List<DistortionSuggestion>) invokePrivate(
                openAiService,
                "heuristicDistortionSuggestions",
                new Class<?>[] { String.class },
                "I always fail, this is the worst disaster, they think I am useless, everyone knows it.");

        assertTrue(suggestions.size() >= 3);
        assertTrue(suggestions.stream().anyMatch(s -> "all-or-nothing".equals(s.getDistortionId())));
        assertTrue(suggestions.stream().anyMatch(s -> "catastrophizing".equals(s.getDistortionId())));
        assertTrue(suggestions.stream().anyMatch(s -> "mind-reading".equals(s.getDistortionId())));
        assertEquals(
                suggestions.stream()
                        .sorted(Comparator.comparingDouble(DistortionSuggestion::getConfidence).reversed())
                        .toList(),
                suggestions);
    }

    @Test
    void buildCrisisResponse_usesResourceSetBySeverity() throws Exception {
        CrisisDetectionResponse significant = (CrisisDetectionResponse) invokePrivate(
                openAiService,
                "buildCrisisResponse",
                new Class<?>[] { List.class, SeverityLevel.class },
                List.of("keyword"),
                SeverityLevel.SIGNIFICANT);

        CrisisDetectionResponse moderate = (CrisisDetectionResponse) invokePrivate(
                openAiService,
                "buildCrisisResponse",
                new Class<?>[] { List.class, SeverityLevel.class },
                List.of("keyword"),
                SeverityLevel.MODERATE);

        assertTrue(significant.recommendedNextSteps().stream().anyMatch(step -> step.contains("988")));
        assertTrue(moderate.recommendedNextSteps().stream().noneMatch(step -> step.contains("988")));
    }

    @Test
    void mapRiskLevel_and_maxSeverity_coverExpectedMappings() throws Exception {
        SeverityLevel high = (SeverityLevel) invokePrivate(
                openAiService,
                "mapRiskLevel",
                new Class<?>[] { String.class },
                "HIGH");
        SeverityLevel medium = (SeverityLevel) invokePrivate(
                openAiService,
                "mapRiskLevel",
                new Class<?>[] { String.class },
                "medium");
        SeverityLevel none = (SeverityLevel) invokePrivate(
                openAiService,
                "mapRiskLevel",
                new Class<?>[] { String.class },
                (Object) null);

        SeverityLevel merged = (SeverityLevel) invokePrivate(
                openAiService,
                "maxSeverity",
                new Class<?>[] { SeverityLevel.class, SeverityLevel.class },
                medium,
                high);

        assertEquals(SeverityLevel.SIGNIFICANT, high);
        assertEquals(SeverityLevel.MODERATE, medium);
        assertEquals(SeverityLevel.MILD, none);
        assertEquals(SeverityLevel.SIGNIFICANT, merged);
    }

    @Test
    void generateInsights_withNoEntries_returnsZeroedInsights() {
        UUID userId = UUID.randomUUID();
        when(diaryEntryRepository.findByUser_IdAndDeletedFalse(userId)).thenReturn(List.of());

        DiaryInsights insights = openAiService.generateInsights(userId);

        assertNotNull(insights);
        assertEquals(0, insights.getTotalEntries());
        assertEquals(0.0d, insights.getAverageMoodImprovement());
        assertEquals(0, insights.getBestMoodImprovement());
    }

    @Test
    void fallbackReframingPrompts_usesBalancedDefaultWhenNoDistortionIdsProvided() throws Exception {
        @SuppressWarnings("unchecked")
        List<String> prompts = (List<String>) invokePrivate(
                openAiService,
                "fallbackReframingPrompts",
                new Class<?>[] { String.class, List.class },
                "I always fail",
                List.of());

        assertEquals(3, prompts.size());
        assertTrue(prompts.get(2).contains("more balanced way"));
    }

    private Object invokePrivate(Object target, String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw ex;
        }
    }

    private OpenAiService createConfiguredService(RestClient.Builder builder) {
        return new OpenAiService(
                builder,
                new ObjectMapper(),
                ragContextBuilder,
                userSessionRepository,
                diaryEntryRepository,
                chatMessageRepository,
                cognitiveDistortionRepository,
                "test-key",
                "gpt-4o-mini",
                "https://api.openai.com/v1");
    }

    private DiaryEntry diaryEntry(int moodBefore, int moodAfter) {
        DiaryEntry entry = new DiaryEntry();
        entry.setMoodBefore(moodBefore);
        entry.setMoodAfter(moodAfter);
        return entry;
    }

    private ChatMessage chatMessage(ChatRole role, String content) {
        ChatMessage message = new ChatMessage();
        message.setId(UUID.randomUUID());
        message.setRole(role);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
}
