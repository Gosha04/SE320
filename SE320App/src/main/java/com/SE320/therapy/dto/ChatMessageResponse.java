package com.SE320.therapy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageResponse(
    UUID messageId,
    String role,
    String content,
    String modality,
    LocalDateTime timestamp
) {}
