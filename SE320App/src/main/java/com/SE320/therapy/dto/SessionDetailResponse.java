package com.SE320.therapy.dto;

import java.util.List;

public record SessionDetailResponse(
    Long sessionId,
    String title,
    String description,
    Integer durationMinutes,
    Integer orderIndex,
    String moduleName,
    List<String> objectives,
    List<String> modalities
) {}
