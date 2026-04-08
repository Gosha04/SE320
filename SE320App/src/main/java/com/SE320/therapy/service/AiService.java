package com.SE320.therapy.service;

import java.util.List;
import java.util.UUID;

import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;

public interface AiService {

    String generateResponse(UUID sessionId, String userMessage);

    List<DistortionSuggestion> analyzeThought(String automaticThought);

    List<String> generateReframingPrompts(String thought, List<String> distortionIds);

    CrisisDetectionResponse detectCrisis(String text);

    DiaryInsights generateInsights(UUID userId);

    String summarizeSession(UUID sessionId);
}
