package com.SE320.therapy.mcp;

import com.SE320.therapy.dto.CrisisDetectionRequest;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;
import com.SE320.therapy.dto.objects.InteractionModality;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.repository.UserSessionRepository;
import com.SE320.therapy.service.AiService;
import com.SE320.therapy.service.CrisisService;
import com.SE320.therapy.service.DashboardService;
import com.SE320.therapy.service.DiaryService;
import com.SE320.therapy.service.SessionService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;

@Component
public class TherapyToolsProvider {

    private static final int DEFAULT_MOOD = 5;

    private final SessionService sessionService;
    private final DiaryService diaryService;
    private final DashboardService dashboardService;
    private final CrisisService crisisService;
    private final AiService aiService;
    private final UserSessionRepository userSessionRepository;

    public TherapyToolsProvider(SessionService sessionService,
                                DiaryService diaryService,
                                DashboardService dashboardService,
                                CrisisService crisisService,
                                AiService aiService,
                                UserSessionRepository userSessionRepository) {
        this.sessionService = sessionService;
        this.diaryService = diaryService;
        this.dashboardService = dashboardService;
        this.crisisService = crisisService;
        this.aiService = aiService;
        this.userSessionRepository = userSessionRepository;
    }

    @McpTool(name = "start_session", description = "Start a new CBT session for a user.")
    public SessionRunResponse startSession(
            @McpToolParam(description = "User UUID", required = true) String userId,
            @McpToolParam(description = "CBT session module id", required = true) Long sessionId) {
        return sessionService.startSession(sessionId, new StartSessionRequest(parseUuid(userId), DEFAULT_MOOD));
    }

    @McpTool(name = "chat_in_session", description = "Send a message in an active CBT session.")
    public SessionChatResponse chatInSession(
            @McpToolParam(description = "Active userSessionId UUID returned by start_session", required = true) String sessionId,
            @McpToolParam(description = "User chat message", required = true) String message) {
        UserSession activeSession = getUserSession(sessionId);
        return sessionService.sendChatMessage(
                activeSession.getCbtSession().getSessionId(),
                new SendChatMessageRequest(activeSession.getUser().getId(), message, InteractionModality.TEXT));
    }

    @McpTool(name = "end_session", description = "End an active CBT session and generate a summary.")
    public SessionRunResponse endSession(
            @McpToolParam(description = "Active userSessionId UUID returned by start_session", required = true) String sessionId,
            @McpToolParam(description = "Reason for ending the session", required = false) String reason) {
        UserSession activeSession = getUserSession(sessionId);
        return sessionService.endSession(
                activeSession.getCbtSession().getSessionId(),
                new EndSessionRequest(activeSession.getUser().getId(), DEFAULT_MOOD));
    }

    @McpTool(name = "get_session_library", description = "List available CBT session modules.")
    public List<SessionLibraryItemResponse> getSessionLibrary(
            @McpToolParam(description = "User UUID for client context", required = false) String userId) {
        return sessionService.getSessionLibrary();
    }

    @McpTool(name = "get_session_history", description = "View a user's past CBT sessions.")
    public List<SessionRunResponse> getSessionHistory(
            @McpToolParam(description = "User UUID", required = true) String userId) {
        return sessionService.getSessionHistory(parseUuid(userId));
    }

    @McpTool(name = "create_diary_entry", description = "Create a thought diary entry.")
    public DiaryEntryResponse createDiaryEntry(
            @McpToolParam(description = "User UUID", required = true) String userId,
            @McpToolParam(description = "Situation that triggered the thought", required = true) String situation,
            @McpToolParam(description = "Automatic thought to record", required = true) String automaticThought,
            @McpToolParam(description = "Emotion labels or intensity notes", required = true) String emotions) {
        String alternativeThought = "Initial emotions noted through MCP: " + nullToBlank(emotions);
        return diaryService.createEntry(
                parseUuid(userId),
                new DiaryEntryCreateRequest(situation, automaticThought, alternativeThought, DEFAULT_MOOD, DEFAULT_MOOD));
    }

    @McpTool(name = "analyze_thought", description = "Analyze an automatic thought for cognitive distortions.")
    public List<DistortionSuggestion> analyzeThought(
            @McpToolParam(description = "Automatic thought text", required = true) String thought) {
        return diaryService.suggestDistortions(thought);
    }

    @McpTool(name = "suggest_reframing", description = "Generate CBT reframing prompts for a thought.")
    public List<String> suggestReframing(
            @McpToolParam(description = "Automatic thought text", required = true) String thought,
            @McpToolParam(description = "Relevant cognitive distortion ids", required = false) List<String> distortionIds) {
        return aiService.generateReframingPrompts(thought, distortionIds == null ? List.of() : distortionIds);
    }

    @McpTool(name = "detect_crisis", description = "Analyze text for crisis indicators.")
    public CrisisDetectionResponse detectCrisis(
            @McpToolParam(description = "Text to analyze for crisis risk", required = true) String text) {
        return crisisService.detectCrisisIndicators(new CrisisDetectionRequest(text, List.of()));
    }

    @McpTool(name = "get_weekly_progress", description = "Get a user's weekly progress summary.")
    public Object getWeeklyProgress(
            @McpToolParam(description = "User UUID", required = true) String userId) {
        return dashboardService.getWeeklyProgress(parseUuid(userId));
    }

    @McpTool(name = "get_insights", description = "Get AI-generated insights from diary entries.")
    public DiaryInsights getInsights(
            @McpToolParam(description = "User UUID", required = true) String userId) {
        return diaryService.getInsights(parseUuid(userId));
    }

    @McpTool(name = "get_coping_strategies", description = "Retrieve CBT coping and de-escalation strategies.")
    public List<String> getCopingStrategies() {
        return crisisService.copingStrategies();
    }

    private UserSession getUserSession(String sessionId) {
        return userSessionRepository.findById(parseUuid(sessionId))
                .orElseThrow(() -> new IllegalArgumentException("Active user session not found: " + sessionId));
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UUID value is required.");
        }
        return UUID.fromString(value.trim());
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value.trim();
    }
}
