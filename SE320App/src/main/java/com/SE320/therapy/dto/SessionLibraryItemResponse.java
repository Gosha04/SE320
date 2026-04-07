package com.SE320.therapy.dto;

import java.util.List;

public record SessionLibraryItemResponse(
    Long sessionId,
    String title,
    String description,
    Integer durationMinutes,
    Integer orderIndex,
    List<String> modalities
) {}
