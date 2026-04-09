package com.SE320.therapy.service;

import java.io.Serializable;
import java.util.UUID;

import com.SE320.therapy.dto.objects.UserType;

public record AuthenticatedUser(
    UUID id,
    String email,
    String firstName,
    String lastName,
    UserType userType
) implements Serializable {}
