package com.SE320.therapy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.SE320.therapy.dto.objects.UserSessionStatus;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.exception.ApiExceptionHandler;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.repository.UserSessionRepository;
import com.SE320.therapy.service.SessionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionWorkflowIntegrationTest {

    private MockMvc mockMvc;
    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private UserSessionRepository userSessionRepository;
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        userSessionRepository = mock(UserSessionRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);

        SessionService sessionService = new SessionService(
            sessionRepository,
            userRepository,
            userSessionRepository,
            chatMessageRepository
        );

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new SessionController(sessionService))
            .setControllerAdvice(new ApiExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void sessionWorkflow_startChatEnd_usesRealControllerAndService() throws Exception {
        UUID userId = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
        CBTSession librarySession = new CBTSession();
        librarySession.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1"));
        librarySession.setSessionId(1001L);
        librarySession.setTitle("Thought Record Practice");
        librarySession.setDescription("Intro");

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPasswordHash("hash");

        AtomicReference<UserSession> storedSession = new AtomicReference<>();

        when(sessionRepository.findLibrarySessionBySessionId(1001L)).thenReturn(Optional.of(librarySession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId, 1001L, UserSessionStatus.IN_PROGRESS))
            .thenAnswer(invocation -> Optional.ofNullable(storedSession.get())
                .filter(session -> session.getStatus() == UserSessionStatus.IN_PROGRESS));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> {
            UserSession session = invocation.getArgument(0);
            if (session.getId() == null) {
                session.setId(UUID.randomUUID());
            }
            if (session.getStartedAt() == null) {
                session.setStartedAt(LocalDateTime.now());
            }
            storedSession.set(session);
            return session;
        });
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

        mockMvc.perform(post("/sessions/1001/start")
                .contentType("application/json")
                .content("""
                    {
                      "userId": "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb",
                      "moodBefore": 6
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.sessionId").value(1001))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(post("/sessions/1001/chat")
                .contentType("application/json")
                .content("""
                    {
                      "userId": "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb",
                      "message": "I feel overwhelmed at work",
                      "modality": "TEXT"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assistantMessage.role").value("ASSISTANT"));

        mockMvc.perform(post("/sessions/1001/end")
                .contentType("application/json")
                .content("""
                    {
                      "userId": "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb",
                      "moodAfter": 7
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.moodAfter").value(7));
    }
}
