package com.SE320.therapy.dto;

import com.SE320.therapy.dto.objects.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotNull(message = "userType is required")
    UserType userType,
    @NotBlank(message = "firstName is required")
    String firstName,
    @NotBlank(message = "lastName is required")
    String lastName,
    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    String email,
    @NotBlank(message = "password is required")
    String password,
    String phoneNumber
) {}
