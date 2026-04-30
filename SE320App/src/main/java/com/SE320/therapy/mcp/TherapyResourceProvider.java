package com.SE320.therapy.mcp;

import com.SE320.therapy.repository.CognitiveDistortionRepository;
import com.SE320.therapy.service.CrisisService;
import com.SE320.therapy.service.DashboardService;
import com.SE320.therapy.service.DiaryService;
import com.SE320.therapy.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springaicommunity.mcp.annotation.McpResource;

@Component
public class TherapyResourceProvider {

    private final SessionService sessionService;
    private final DiaryService diaryService;
    private final DashboardService dashboardService;
    private final CrisisService crisisService;
    private final CognitiveDistortionRepository cognitiveDistortionRepository;
    private final ObjectMapper objectMapper;

    public TherapyResourceProvider(SessionService sessionService,
                                   DiaryService diaryService,
                                   DashboardService dashboardService,
                                   CrisisService crisisService,
                                   CognitiveDistortionRepository cognitiveDistortionRepository,
                                   ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.diaryService = diaryService;
        this.dashboardService = dashboardService;
        this.crisisService = crisisService;
        this.cognitiveDistortionRepository = cognitiveDistortionRepository;
        this.objectMapper = objectMapper;
    }

    @McpResource(uri = "therapy://sessions/{sessionId}", name = "Session details", description = "CBT session detail JSON")
    public String sessionDetails(String sessionId) {
        return toJson(sessionService.getSessionDetail(Long.parseLong(sessionId)));
    }

    @McpResource(uri = "therapy://diary/{userId}", name = "User diary entries", description = "Diary entry summaries for a user")
    public String diaryEntries(String userId) {
        return toJson(diaryService.getEntries(UUID.fromString(userId), Pageable.unpaged()).getContent());
    }

    @McpResource(uri = "therapy://diary/entry/{entryId}", name = "Diary entry detail", description = "Single diary entry detail JSON")
    public String diaryEntryDetail(String entryId) {
        return toJson(diaryService.getEntryDetail(UUID.fromString(entryId)));
    }

    @McpResource(uri = "therapy://progress/{userId}", name = "Progress overview", description = "Dashboard progress JSON")
    public String progressOverview(String userId) {
        return toJson(dashboardService.getDashboard(UUID.fromString(userId)));
    }

    @McpResource(uri = "therapy://distortions", name = "Cognitive distortions", description = "All cognitive distortion definitions")
    public String distortions() {
        return toJson(cognitiveDistortionRepository.findAll());
    }

    @McpResource(uri = "therapy://crisis/resources", name = "Crisis resources", description = "Emergency crisis support data")
    public String crisisResources() {
        return toJson(crisisService.emergencyResources());
    }

    @McpResource(uri = "therapy://safety-plan/{userId}", name = "Safety plan", description = "User safety plan JSON")
    public String safetyPlan(String userId) {
        return toJson(crisisService.safetyPlan(UUID.fromString(userId)));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize MCP resource.", ex);
        }
    }
}
