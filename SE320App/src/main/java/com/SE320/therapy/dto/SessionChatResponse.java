package com.SE320.therapy.dto;

import java.util.UUID;

public record SessionChatResponse(
    UUID userSessionId,
    Long sessionId,
    ChatMessageResponse userMessage,
    ChatMessageResponse assistantMessage
) {}
