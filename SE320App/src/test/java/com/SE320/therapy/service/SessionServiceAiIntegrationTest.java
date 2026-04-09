package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.objects.UserSessionStatus;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.repository.UserSessionRepository;

class SessionServiceAiIntegrationTest {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private UserSessionRepository userSessionRepository;
    private ChatMessageRepository chatMessageRepository;
    private AiService aiService;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        userSessionRepository = mock(UserSessionRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        aiService = mock(AiService.class);
        sessionService = new SessionService(
                sessionRepository,
                userRepository,
                userSessionRepository,
                chatMessageRepository,
                aiService);
    }

    @Test
    void endSession_savesAiSummaryMessageBeforeCompleting() {
        UUID userId = UUID.randomUUID();
        Long sessionId = 1001L;

        User user = new User();
        user.setId(userId);

        CBTSession cbtSession = new CBTSession();
        cbtSession.setSessionId(sessionId);
        cbtSession.setTitle("Thought Record Practice");

        UserSession userSession = new UserSession();
        userSession.setId(UUID.randomUUID());
        userSession.setUser(user);
        userSession.setCbtSession(cbtSession);
        userSession.setStatus(UserSessionStatus.IN_PROGRESS);

        when(sessionRepository.findLibrarySessionBySessionId(sessionId)).thenReturn(Optional.of(cbtSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId,
                sessionId,
                UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(userSession));
        when(aiService.summarizeSession(userSession.getId())).thenReturn("The session focused on identifying one balanced thought.");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userSessionRepository.save(userSession)).thenReturn(userSession);

        sessionService.endSession(sessionId, new EndSessionRequest(userId, 7));

        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(userSessionRepository).save(userSession);
        assertEquals(UserSessionStatus.COMPLETED, userSession.getStatus());
        assertEquals(7, userSession.getMoodAfter());
    }
}
