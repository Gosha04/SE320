package com.SE320.therapy.dto;

import java.util.List;

import com.SE320.therapy.dto.objects.SeverityLevel;

public record CrisisDetectionResponse(
    boolean crisisDetected,
    SeverityLevel severityLevel,
    List<String> matchedIndicators,
    List<String> recommendedNextSteps
) {}
