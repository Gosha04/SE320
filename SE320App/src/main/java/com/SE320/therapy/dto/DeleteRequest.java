package com.SE320.therapy.dto;

import java.util.UUID;

public record DeleteRequest(
    UUID userId,
    String email,
    String password
) {}
