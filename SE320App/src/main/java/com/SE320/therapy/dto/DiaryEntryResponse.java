package com.SE320.therapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiaryEntryResponse(
    UUID id,
    String message,
    LocalDateTime createdAt
) {}