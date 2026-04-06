package com.SE320.therapy.dto;

import java.util.UUID;

import com.SE320.therapy.objects.InteractionModality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendChatMessageRequest(
    @NotNull(message = "userId is required")
    UUID userId,
    @NotBlank(message = "message is required")
    String message,
    InteractionModality modality
) {}
