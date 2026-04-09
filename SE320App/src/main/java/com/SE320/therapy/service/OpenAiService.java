package com.SE320.therapy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.SE320.therapy.service.rag.RagContextBuilder;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.objects.SeverityLevel;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.CognitiveDistortionRepository;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.UserSessionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAiService implements AiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile("```(?:json)?\\s*(.*?)```", Pattern.DOTALL);
    private static final List<String> CRISIS_RESOURCES = List.of(
            "Call or text 988 for the Suicide & Crisis Lifeline.",
            "Text HOME to 741741 to connect with the Crisis Text Line.",
            "Call 911 or go to the nearest emergency room if there is immediate danger.");
    private static final List<String> CRISIS_KEYWORDS = List.of(
            "suicide",
            "kill myself",
            "end it all",
            "no reason to live",
            "better off dead",
            "can't go on",
            "want to die",
            "hurt myself",
            "self-harm");
    private static final int MAX_TRANSCRIPT_CHARS = 12000;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final RagContextBuilder ragContextBuilder;
    private final UserSessionRepository userSessionRepository;
    private final DiaryEntryRepository diaryEntryRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CognitiveDistortionRepository cognitiveDistortionRepository;
    private final String apiKey;
    private final String model;

    public OpenAiService(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            RagContextBuilder ragContextBuilder,
            UserSessionRepository userSessionRepository,
            DiaryEntryRepository diaryEntryRepository,
            ChatMessageRepository chatMessageRepository,
            CognitiveDistortionRepository cognitiveDistortionRepository,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.chat.model:gpt-4o-mini}") String model,
            @Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
        this.ragContextBuilder = ragContextBuilder;
        this.userSessionRepository = userSessionRepository;
        this.diaryEntryRepository = diaryEntryRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.cognitiveDistortionRepository = cognitiveDistortionRepository;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
    }

    @Override
    public String generateResponse(UUID sessionId, String userMessage) {
        UserSession userSession = userSessionRepository.findById(sessionId).orElse(null);
        UUID userId = userSession == null || userSession.getUser() == null ? null : userSession.getUser().getId();
        String context = userId == null ? "" : ragContextBuilder.buildContext(userId, sessionId, userMessage);

        CrisisDetectionResponse crisisResponse = detectCrisis(userMessage);
        if (crisisResponse.crisisDetected() && crisisResponse.severityLevel() == SeverityLevel.SIGNIFICANT) {
            log.warn("Using crisis safety response instead of standard chat generation for sessionId={}", sessionId);
            return "I'm really glad you said that. Your safety matters most right now. "
                    + "Please contact immediate support now: " + String.join(" ", CRISIS_RESOURCES);
        }

        if (!isConfigured()) {
            log.warn("OPENAI_API_KEY is not configured. Using fallback therapeutic response for sessionId={}", sessionId);
            return fallbackTherapeuticResponse(userMessage, context);
        }

        String prompt = """
                You are a compassionate AI therapy assistant specializing in Cognitive Behavioral Therapy (CBT) for workplace burnout recovery.
                Maintain an empathetic, non-judgmental, and supportive tone.
                Use Socratic questioning and practical CBT guidance.
                Never provide a medical diagnosis.
                If crisis signals appear, prioritize safety and encourage immediate support.

                Context about the user and relevant CBT knowledge:
                %s

                User message:
                %s
                """.formatted(blankToNone(context), userMessage);

        try {
            log.info("Using OpenAI chat completion for therapeutic response. sessionId={} model={}", sessionId, model);
            return callForText("You are a compassionate CBT assistant for burnout recovery.", prompt);
        } catch (RuntimeException ex) {
            log.warn("Falling back to local therapeutic response for sessionId={} because OpenAI call failed: {}", sessionId, ex.getMessage());
            log.debug("OpenAI chat failure details", ex);
            return fallbackTherapeuticResponse(userMessage, context);
        }
    }

    @Override
    public List<DistortionSuggestion> analyzeThought(String automaticThought) {
        if (automaticThought == null || automaticThought.isBlank()) {
            return List.of();
        }

        List<String> distortionIds = cognitiveDistortionRepository.findAll()
                .stream()
                .map(distortion -> distortion.getId())
                .sorted()
                .toList();

        if (!isConfigured()) {
            log.warn("OPENAI_API_KEY is not configured. Using heuristic distortion analysis.");
            return heuristicDistortionSuggestions(automaticThought);
        }

        String prompt = """
                Analyze the following automatic thought for cognitive distortions.
                Return a JSON array of objects with fields distortionId, confidence, and reasoning.
                Only use distortion IDs from this list: %s

                Thought: "%s"
                """.formatted(distortionIds, automaticThought.replace("\"", "\\\""));

        try {
            log.info("Using OpenAI for distortion analysis with model={}", model);
            String content = callForText(
                    "You identify CBT cognitive distortions and respond with valid JSON only.",
                    prompt);
            return parseDistortionSuggestions(content, distortionIds);
        } catch (RuntimeException ex) {
            log.warn("Falling back to heuristic distortion analysis because OpenAI call failed: {}", ex.getMessage());
            log.debug("OpenAI distortion analysis failure details", ex);
            return heuristicDistortionSuggestions(automaticThought);
        }
    }

    @Override
    public List<String> generateReframingPrompts(String thought, List<String> distortionIds) {
        if (thought == null || thought.isBlank()) {
            return List.of();
        }

        if (!isConfigured()) {
            log.warn("OPENAI_API_KEY is not configured. Using fallback reframing prompts.");
            return fallbackReframingPrompts(thought, distortionIds);
        }

        String prompt = """
                Generate a JSON array of 3 short CBT reframing prompts for this thought.
                Return only JSON. Do not include markdown, code fences, headings, or explanatory text.
                Example format:
                ["What evidence supports this thought?", "What evidence points in a different direction?", "What is a more balanced way to describe this situation?"]
                Thought: "%s"
                Related distortion ids: %s
                Each prompt should help the user examine evidence, consider alternatives, or find a balanced next step.
                """.formatted(thought.replace("\"", "\\\""), distortionIds == null ? List.of() : distortionIds);

        try {
            log.info("Using OpenAI for reframing prompt generation with model={}", model);
            String content = callForText(
                    "You generate concise CBT reframing prompts and respond with valid JSON only.",
                    prompt);
            return parseStringArray(content);
        } catch (RuntimeException ex) {
            log.warn("Falling back to local reframing prompts because OpenAI call failed: {}", ex.getMessage());
            log.debug("OpenAI reframing prompt failure details", ex);
            return fallbackReframingPrompts(thought, distortionIds);
        }
    }

    @Override
    public CrisisDetectionResponse detectCrisis(String text) {
        String normalized = text == null ? "" : text.toLowerCase(Locale.ROOT);
        List<String> keywordsDetected = CRISIS_KEYWORDS.stream()
                .filter(normalized::contains)
                .toList();

        SeverityLevel keywordSeverity = keywordsDetected.isEmpty() ? SeverityLevel.MILD : SeverityLevel.SIGNIFICANT;
        if (!isConfigured()) {
            log.warn("OPENAI_API_KEY is not configured. Using keyword-only crisis detection.");
            return buildCrisisResponse(keywordsDetected, keywordSeverity);
        }

        String prompt = """
                Analyze the following text for crisis indicators.
                Return a JSON object with fields riskLevel, keywordsDetected, recommendedAction, and reasoning.
                riskLevel must be one of: none, low, medium, high, critical.
                recommendedAction must be one of: none, show_resources, show_crisis_hub, immediate_intervention.

                Text: "%s"
                """.formatted((text == null ? "" : text).replace("\"", "\\\""));

        try {
            log.info("Using OpenAI for crisis detection with model={}", model);
            String content = callForText(
                    "You assess crisis risk conservatively and respond with valid JSON only.",
                    prompt);
            JsonNode root = objectMapper.readTree(extractJson(content));
            List<String> aiKeywords = new ArrayList<>(keywordsDetected);
            if (root.path("keywordsDetected").isArray()) {
                for (JsonNode node : root.path("keywordsDetected")) {
                    String value = node.asText().trim();
                    if (!value.isEmpty() && !aiKeywords.contains(value)) {
                        aiKeywords.add(value);
                    }
                }
            }
            SeverityLevel severity = maxSeverity(keywordSeverity, mapRiskLevel(root.path("riskLevel").asText("none")));
            return buildCrisisResponse(aiKeywords, severity);
        } catch (RuntimeException | IOException ex) {
            log.warn("Falling back to keyword-only crisis detection because OpenAI call failed: {}", ex.getMessage());
            log.debug("OpenAI crisis detection failure details", ex);
            return buildCrisisResponse(keywordsDetected, keywordSeverity);
        }
    }

    @Override
    public DiaryInsights generateInsights(UUID userId) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndDeletedFalse(userId);
        if (entries.isEmpty()) {
            return new DiaryInsights(0, 0.0, 0);
        }

        int totalImprovement = 0;
        int bestImprovement = Integer.MIN_VALUE;
        for (DiaryEntry entry : entries) {
            int improvement = entry.getMoodAfter() - entry.getMoodBefore();
            totalImprovement += improvement;
            bestImprovement = Math.max(bestImprovement, improvement);
        }

        double averageImprovement = (double) totalImprovement / entries.size();
        return new DiaryInsights(entries.size(), averageImprovement, bestImprovement);
    }

    @Override
    public String summarizeSession(UUID sessionId) {
        if (sessionId == null) {
            return "Session completed. No transcript was available for summarization.";
        }

        List<ChatMessage> messages = chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId);
        if (messages == null || messages.isEmpty()) {
            return "Session completed. No transcript was available for summarization.";
        }

        String transcript = buildTranscript(messages);
        if (transcript.isBlank()) {
            return "Session completed. No transcript was available for summarization.";
        }

        if (!isConfigured()) {
            log.warn("OPENAI_API_KEY is not configured. Using fallback session summary for sessionId={}", sessionId);
            return fallbackSessionSummary(transcript);
        }

        String prompt = """
                Summarize this CBT session in 3-4 concise sentences.
                Include the main theme, one helpful CBT technique, and one realistic next step.

                Transcript:
                %s
                """.formatted(transcript);

        try {
            log.info("Using OpenAI for session summarization. sessionId={} model={}", sessionId, model);
            return callForText("You summarize CBT sessions clearly and concisely.", prompt);
        } catch (RuntimeException ex) {
            log.warn("Falling back to local session summary for sessionId={} because OpenAI call failed: {}", sessionId, ex.getMessage());
            log.debug("OpenAI session summary failure details", ex);
            return fallbackSessionSummary(transcript);
        }
    }

    private String callForText(String systemPrompt, String userPrompt) {
        OpenAiChatRequest request = new OpenAiChatRequest(
                model,
                List.of(
                        new OpenAiMessage("system", systemPrompt),
                        new OpenAiMessage("user", userPrompt)),
                0.4);

        String responseBody = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(String.class);
        if (responseBody == null || responseBody.isBlank()) {
            throw new IllegalStateException("OpenAI response body was empty.");
        }

        JsonNode response;
        try {
            response = objectMapper.readTree(responseBody);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse OpenAI response body.", ex);
        }

        if (response == null || !response.path("choices").isArray() || response.path("choices").size() == 0) {
            throw new IllegalStateException("OpenAI response did not contain any choices.");
        }

        JsonNode firstChoice = response.path("choices").get(0);
        String content = firstChoice.path("message").path("content").asText(null);
        if (content == null || content.isBlank()) {
            // Some model/tool responses may return a direct text field.
            content = firstChoice.path("text").asText(null);
        }
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("OpenAI response content was empty.");
        }
        return content.trim();
    }

    private String buildTranscript(List<ChatMessage> messages) {
        List<String> lines = messages.stream()
                .filter(message -> message != null)
                .filter(message -> message.getContent() != null && !message.getContent().trim().isEmpty())
                .map(message -> {
                    String role = message.getRole() == null ? "UNKNOWN" : message.getRole().name();
                    return role + ": " + message.getContent().trim();
                })
                .toList();

        if (lines.isEmpty()) {
            return "";
        }

        String transcript = String.join("\n", lines);
        if (transcript.length() <= MAX_TRANSCRIPT_CHARS) {
            return transcript;
        }

        // Keep the most recent segment to avoid very large prompts.
        return transcript.substring(transcript.length() - MAX_TRANSCRIPT_CHARS);
    }

    private List<DistortionSuggestion> parseDistortionSuggestions(String responseContent, List<String> allowedIds) {
        try {
            JsonNode root = objectMapper.readTree(extractJson(responseContent));
            JsonNode suggestionsNode = root;
            if (root.isObject()) {
                if (root.has("distortions")) {
                    suggestionsNode = root.get("distortions");
                } else if (root.has("suggestions")) {
                    suggestionsNode = root.get("suggestions");
                } else if (root.has("results")) {
                    suggestionsNode = root.get("results");
                }
            }

            List<DistortionSuggestion> suggestions = objectMapper.convertValue(
                    suggestionsNode,
                    new TypeReference<List<DistortionSuggestion>>() {
                    });
            return suggestions.stream()
                    .filter(suggestion -> suggestion.getDistortionId() != null)
                    .filter(suggestion -> allowedIds.isEmpty() || allowedIds.contains(suggestion.getDistortionId()))
                    .sorted(Comparator.comparingDouble(DistortionSuggestion::getConfidence).reversed())
                    .toList();
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to parse distortion suggestions.", ex);
        }
    }

    private List<String> parseStringArray(String responseContent) {
        try {
            String normalized = extractJson(responseContent);
            JsonNode root = objectMapper.readTree(normalized);
            if (root.isArray()) {
                return objectMapper.convertValue(root, new TypeReference<List<String>>() {
                });
            }

            if (root.isObject()) {
                if (root.has("prompts") && root.get("prompts").isArray()) {
                    return objectMapper.convertValue(root.get("prompts"), new TypeReference<List<String>>() {
                    });
                }
                if (root.has("items") && root.get("items").isArray()) {
                    return objectMapper.convertValue(root.get("items"), new TypeReference<List<String>>() {
                    });
                }
                if (root.has("reframingPrompts") && root.get("reframingPrompts").isArray()) {
                    return objectMapper.convertValue(root.get("reframingPrompts"), new TypeReference<List<String>>() {
                    });
                }
            }

            throw new IllegalStateException("Response JSON did not contain a prompt array.");
        } catch (JsonProcessingException ex) {
            List<String> lineItems = parseLineList(responseContent);
            if (!lineItems.isEmpty()) {
                return lineItems;
            }
            throw new IllegalStateException("Unable to parse string array response.", ex);
        }
    }

    private String extractJson(String content) {
        String trimmed = content == null ? "" : content.trim();
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(trimmed);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return trimmed;
    }

    private List<String> parseLineList(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }

        return content.lines()
                .map(String::trim)
                .map(line -> line.replaceFirst("^[-*•]\\s*", ""))
                .map(line -> line.replaceFirst("^\\d+\\.\\s*", ""))
                .filter(line -> !line.isBlank())
                .toList();
    }

    private boolean isConfigured() {
        return !apiKey.isBlank();
    }

    private String fallbackTherapeuticResponse(String userMessage, String context) {
        String contextLead = context == null || context.isBlank()
                ? ""
                : " I’m keeping your recent diary and session context in mind as we look at this.";
        return "It sounds like this feels heavy right now." + contextLead
                + " What feels like the hardest part of \"" + userMessage + "\" at the moment?"
                + " We can slow it down, look at the thought behind it, and test one small next step together.";
    }

    private List<DistortionSuggestion> heuristicDistortionSuggestions(String thought) {
        String lowerThought = thought.toLowerCase(Locale.ROOT);
        List<DistortionSuggestion> suggestions = new ArrayList<>();

        if (lowerThought.contains("always") || lowerThought.contains("never") || lowerThought.contains("every time")) {
            suggestions.add(new DistortionSuggestion("all-or-nothing", 0.88, "The thought uses absolute language."));
        }
        if (lowerThought.contains("worst") || lowerThought.contains("ruined") || lowerThought.contains("disaster")) {
            suggestions.add(new DistortionSuggestion("catastrophizing", 0.85, "The thought jumps to the worst-case outcome."));
        }
        if (lowerThought.contains("they think") || lowerThought.contains("they must think") || lowerThought.contains("they hate me")) {
            suggestions.add(new DistortionSuggestion("mind-reading", 0.82, "The thought assumes other people's judgments without direct evidence."));
        }
        if (lowerThought.contains("i feel") && (lowerThought.contains("so it is") || lowerThought.contains("that means it is"))) {
            suggestions.add(new DistortionSuggestion("emotional-reasoning", 0.74, "The thought treats a feeling as proof of fact."));
        }
        if (lowerThought.contains("should") || lowerThought.contains("must")) {
            suggestions.add(new DistortionSuggestion("should-statements", 0.73, "The thought includes rigid internal rules."));
        }
        if (lowerThought.contains("everyone") || lowerThought.contains("nobody")) {
            suggestions.add(new DistortionSuggestion("overgeneralization", 0.7, "The thought generalizes broadly from limited evidence."));
        }

        return suggestions.stream()
                .sorted(Comparator.comparingDouble(DistortionSuggestion::getConfidence).reversed())
                .toList();
    }

    private List<String> fallbackReframingPrompts(String thought, List<String> distortionIds) {
        List<String> prompts = new ArrayList<>();
        prompts.add("What evidence supports this thought, and what evidence points in a different direction?");
        prompts.add("If a friend said \"" + thought + "\", what balanced response would you offer them?");
        if (distortionIds != null && !distortionIds.isEmpty()) {
            prompts.add("How might " + String.join(", ", distortionIds) + " be shaping the way this situation feels right now?");
        } else {
            prompts.add("What is a more balanced way to describe this situation without ignoring the hard parts?");
        }
        return prompts;
    }

    private CrisisDetectionResponse buildCrisisResponse(List<String> indicators, SeverityLevel severityLevel) {
        List<String> recommendedNextSteps = severityLevel == SeverityLevel.SIGNIFICANT
                ? CRISIS_RESOURCES
                : List.of(
                        "Pause and move to a safer, quieter space if you can.",
                        "Reach out to a trusted person and stay connected.",
                        "Use grounding or slow breathing while deciding on your next support step.");
        return new CrisisDetectionResponse(
                !indicators.isEmpty(),
                severityLevel,
                indicators,
                recommendedNextSteps);
    }

    private SeverityLevel mapRiskLevel(String riskLevel) {
        return switch (riskLevel == null ? "none" : riskLevel.toLowerCase(Locale.ROOT)) {
            case "critical", "high" -> SeverityLevel.SIGNIFICANT;
            case "medium", "low" -> SeverityLevel.MODERATE;
            default -> SeverityLevel.MILD;
        };
    }

    private SeverityLevel maxSeverity(SeverityLevel left, SeverityLevel right) {
        if (left == SeverityLevel.SIGNIFICANT || right == SeverityLevel.SIGNIFICANT) {
            return SeverityLevel.SIGNIFICANT;
        }
        if (left == SeverityLevel.MODERATE || right == SeverityLevel.MODERATE) {
            return SeverityLevel.MODERATE;
        }
        return SeverityLevel.MILD;
    }

    private String fallbackSessionSummary(String transcript) {
        String firstLine = transcript.lines().findFirst().orElse("The session focused on a difficult moment.");
        return "The session centered on " + firstLine
                + " A helpful next CBT step is to identify the strongest automatic thought, check the evidence, and choose one manageable action before the next session.";
    }

    private String blankToNone(String value) {
        return value == null || value.isBlank() ? "None available." : value;
    }

    private record OpenAiChatRequest(
            String model,
            List<OpenAiMessage> messages,
            Double temperature) {
    }

    private record OpenAiMessage(
            String role,
            String content) {
    }
}
