package com.SE320.therapy.dto;

import java.time.Month;
import java.util.UUID;

public record AchievementResponse(
    UUID id,
    UUID userId,
    String title,
    String description,
    boolean unlocked,
    Month unlockedMonth
) {}
