package com.SE320.therapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionRunResponse(
    UUID userSessionId,
    UUID userId,
    Long sessionId,
    String title,
    String status,
    Integer moodBefore,
    Integer moodAfter,
    LocalDateTime startedAt,
    LocalDateTime endedAt
) {}
