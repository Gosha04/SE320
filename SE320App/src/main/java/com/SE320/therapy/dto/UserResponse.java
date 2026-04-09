package com.SE320.therapy.dto;

import java.util.UUID;

import com.SE320.therapy.dto.objects.UserType;

public record UserResponse(
    UUID id,
    UserType userType,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    boolean online
) {}
