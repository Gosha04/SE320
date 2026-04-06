package com.SE320.therapy.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StartSessionRequest(
    @NotNull(message = "userId is required")
    UUID userId,
    @Min(value = 1, message = "moodBefore must be between 1 and 10")
    @Max(value = 10, message = "moodBefore must be between 1 and 10")
    Integer moodBefore
) {}
