package com.SE320.therapy.service;

import com.SE320.therapy.dto.UserResponse;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UserResponse user
) {}
