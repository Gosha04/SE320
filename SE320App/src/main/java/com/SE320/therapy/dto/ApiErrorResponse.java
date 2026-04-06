package com.SE320.therapy.dto;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
    String code,
    String message,
    List<ApiErrorDetail> details,
    Instant timestamp
) {}
