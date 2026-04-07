package com.SE320.therapy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionDetailResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.exception.ApiException;
import com.SE320.therapy.objects.ChatRole;
import com.SE320.therapy.objects.InteractionModality;
import com.SE320.therapy.objects.UserSessionStatus;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.repository.UserSessionRepository;

class SessionServiceApiTest {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private UserSessionRepository userSessionRepository;
    private ChatMessageRepository chatMessageRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        userSessionRepository = mock(UserSessionRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        sessionService = new SessionService(sessionRepository, userRepository, userSessionRepository, chatMessageRepository);
    }

    @Test
    void getSessionLibrary_mapsLibrarySessions() {
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        when(sessionRepository.findLibrarySessions()).thenReturn(List.of(session));

        List<SessionLibraryItemResponse> library = sessionService.getSessionLibrary();

        assertEquals(1, library.size());
        assertEquals(1001L, library.get(0).sessionId());
        assertEquals("Thought Record Practice", library.get(0).title());
    }

    @Test
    void getSessionDetail_returnsDetail() {
        CBTSession session = librarySession(1002L, "Behavioral Activation Planning");
        session.setObjectives(List.of("Take one small action"));
        when(sessionRepository.findLibrarySessionBySessionId(1002L)).thenReturn(Optional.of(session));

        SessionDetailResponse detail = sessionService.getSessionDetail(1002L);

        assertEquals(1002L, detail.sessionId());
        assertEquals("Behavioral Activation Planning", detail.title());
        assertEquals(1, detail.objectives().size());
    }

    @Test
    void startSession_createsUserSession() {
        UUID userId = UUID.randomUUID();
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        User user = user(userId);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.empty());
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> {
            UserSession userSession = invocation.getArgument(0);
            if (userSession.getId() == null) {
                userSession.setId(UUID.randomUUID());
            }
            if (userSession.getStartedAt() == null) {
                userSession.setStartedAt(LocalDateTime.now());
            }
            return userSession;
        });

        SessionRunResponse response = sessionService.startSession(1001L, new StartSessionRequest(userId, 6));

        assertEquals(userId, response.userId());
        assertEquals(1001L, response.sessionId());
        assertEquals("IN_PROGRESS", response.status());
        assertEquals(6, response.moodBefore());
        assertNotNull(response.userSessionId());
    }

    @Test
    void startSession_throwsConflictWhenActiveSessionExists() {
        UUID userId = UUID.randomUUID();
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        User user = user(userId);
        UserSession activeSession = userSession(user, session, UserSessionStatus.IN_PROGRESS);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(activeSession));

        ApiException exception = assertThrows(
            ApiException.class,
            () -> sessionService.startSession(1001L, new StartSessionRequest(userId, 5))
        );

        assertEquals("ACTIVE_SESSION_EXISTS", exception.getCode());
        verify(userSessionRepository, never()).save(any(UserSession.class));
    }

    @Test
    void sendChatMessage_savesUserAndAssistantMessages() {
        UUID userId = UUID.randomUUID();
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        User user = user(userId);
        UserSession activeSession = userSession(user, session, UserSessionStatus.IN_PROGRESS);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(activeSession));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage message = invocation.getArgument(0);
            if (message.getId() == null) {
                message.setId(UUID.randomUUID());
            }
            if (message.getTimestamp() == null) {
                message.setTimestamp(LocalDateTime.now());
            }
            return message;
        });

        SessionChatResponse response = sessionService.sendChatMessage(
            1001L,
            new SendChatMessageRequest(userId, "I feel stuck at work", InteractionModality.TEXT)
        );

        assertEquals(1001L, response.sessionId());
        assertEquals("USER", response.userMessage().role());
        assertEquals("ASSISTANT", response.assistantMessage().role());
        assertEquals("TEXT", response.assistantMessage().modality());
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
    }

    @Test
    void endSession_updatesActiveSession() {
        UUID userId = UUID.randomUUID();
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        User user = user(userId);
        UserSession activeSession = userSession(user, session, UserSessionStatus.IN_PROGRESS);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(activeSession));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SessionRunResponse response = sessionService.endSession(1001L, new EndSessionRequest(userId, 7));

        assertEquals("COMPLETED", response.status());
        assertEquals(7, response.moodAfter());
        assertNotNull(response.endedAt());
    }

    @Test
    void getSessionHistory_returnsUserSessions() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        UserSession completedSession = userSession(user, session, UserSessionStatus.COMPLETED);
        completedSession.setEndedAt(LocalDateTime.now());

        when(userSessionRepository.findByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of(completedSession));

        List<SessionRunResponse> history = sessionService.getSessionHistory(userId);

        assertEquals(1, history.size());
        assertEquals("COMPLETED", history.get(0).status());
        assertEquals(1001L, history.get(0).sessionId());
    }

    @Test
    void continueSession_returnsActiveSession() {
        UUID userId = UUID.randomUUID();
        User user = user(userId);
        CBTSession session = librarySession(1001L, "Thought Record Practice");
        UserSession activeSession = userSession(user, session, UserSessionStatus.IN_PROGRESS);

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId, 1001L, UserSessionStatus.IN_PROGRESS)).thenReturn(Optional.of(activeSession));

        SessionRunResponse response = sessionService.continueSession(userId, 1001L);

        assertEquals(userId, response.userId());
        assertEquals(1001L, response.sessionId());
        assertEquals("IN_PROGRESS", response.status());
    }

    private CBTSession librarySession(Long sessionId, String title) {
        CBTSession session = new CBTSession();
        session.setId(UUID.randomUUID());
        session.setSessionId(sessionId);
        session.setTitle(title);
        session.setDescription("desc");
        session.setDurationMinutes(20);
        session.setOrderIndex(1);
        return session;
    }

    private User user(UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        return user;
    }

    private UserSession userSession(User user, CBTSession session, UserSessionStatus status) {
        UserSession userSession = new UserSession();
        userSession.setId(UUID.randomUUID());
        userSession.setUser(user);
        userSession.setCbtSession(session);
        userSession.setStatus(status);
        userSession.setStartedAt(LocalDateTime.now().minusMinutes(10));
        return userSession;
    }
}
