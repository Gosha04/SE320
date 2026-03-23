package com.SE320.therapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiaryEntryDetail(
    UUID id,
    UUID userId,
    String situation,
    String automaticThought,
    String alternativeThought,
    int moodBefore,
    int moodAfter,
    LocalDateTime createdAt
) {}