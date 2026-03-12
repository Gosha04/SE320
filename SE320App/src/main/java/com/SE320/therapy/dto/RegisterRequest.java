package com.SE320.therapy.dto;

import com.SE320.therapy.objects.UserType;

public record RegisterRequest(
    UserType userType,
    String firstName,
    String lastName,
    String email,
    String password,
    Integer phoneNumber
) {}
