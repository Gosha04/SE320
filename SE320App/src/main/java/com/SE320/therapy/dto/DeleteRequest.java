package com.SE320.therapy.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteRequest(
    @NotNull(message = "userId is required")
    UUID userId,
    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    String email,
    @NotBlank(message = "password is required")
    String password
) {}
