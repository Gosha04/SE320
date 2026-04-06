package com.SE320.therapy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.ChatMessageResponse;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionDetailResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;
import com.SE320.therapy.exception.ApiException;
import com.SE320.therapy.exception.ApiExceptionHandler;
import com.SE320.therapy.service.SessionService;

class SessionControllerApiTest {

    private MockMvc mockMvc;
    private StubSessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new StubSessionService();
        SessionController controller = new SessionController(sessionService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ApiExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void getSessionLibrary_returnsLibrary() throws Exception {
        sessionService.library = List.of(
            new SessionLibraryItemResponse(1001L, "Understanding Thoughts", "Intro", 20, 1, List.of("COGNITIVE"))
        );

        mockMvc.perform(get("/sessions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].sessionId").value(1001))
            .andExpect(jsonPath("$[0].title").value("Understanding Thoughts"));
    }

    @Test
    void getSessionDetail_returnsDetail() throws Exception {
        sessionService.detail = new SessionDetailResponse(
            1001L,
            "Understanding Thoughts",
            "Intro",
            20,
            1,
            "Foundations",
            List.of("Understand the CBT triangle."),
            List.of("COGNITIVE")
        );

        mockMvc.perform(get("/sessions/1001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.moduleName").value("Foundations"))
            .andExpect(jsonPath("$.objectives[0]").value("Understand the CBT triangle."));
    }

    @Test
    void startSession_validRequest_returnsCreated() throws Exception {
        UUID userId = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
        UUID userSessionId = UUID.fromString("11111111-2222-3333-4444-555555555555");

        sessionService.startResponse = new SessionRunResponse(
            userSessionId,
            userId,
            1001L,
            "Understanding Thoughts",
            "IN_PROGRESS",
            6,
            null,
            LocalDateTime.of(2026, 1, 15, 10, 30),
            null
        );

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
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.moodBefore").value(6));
    }

    @Test
    void startSession_missingUserId_returnsValidationError() throws Exception {
        mockMvc.perform(post("/sessions/1001/start")
                .contentType("application/json")
                .content("""
                    {
                      "moodBefore": 6
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.error.details[0].field").value("userId"));
    }

    @Test
    void sendChatMessage_returnsAssistantReply() throws Exception {
        UUID userSessionId = UUID.fromString("11111111-2222-3333-4444-555555555555");

        sessionService.chatResponse = new SessionChatResponse(
            userSessionId,
            1001L,
            new ChatMessageResponse(
                UUID.fromString("66666666-2222-3333-4444-555555555555"),
                "USER",
                "I feel stuck",
                "TEXT",
                LocalDateTime.of(2026, 1, 15, 10, 31)
            ),
            new ChatMessageResponse(
                UUID.fromString("77777777-2222-3333-4444-555555555555"),
                "ASSISTANT",
                "Let's explore that thought.",
                "TEXT",
                LocalDateTime.of(2026, 1, 15, 10, 31, 30)
            )
        );

        mockMvc.perform(post("/sessions/1001/chat")
                .contentType("application/json")
                .content("""
                    {
                      "userId": "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb",
                      "message": "I feel stuck",
                      "modality": "TEXT"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assistantMessage.role").value("ASSISTANT"))
            .andExpect(jsonPath("$.assistantMessage.content").value("Let's explore that thought."));
    }

    @Test
    void endSession_notFound_returnsStructuredError() throws Exception {
        sessionService.endException = new ApiException(
            org.springframework.http.HttpStatus.NOT_FOUND,
            "SESSION_NOT_FOUND",
            "No active session was found for the given user and session.",
            List.of(new ApiErrorDetail("sessionId", "No active user session matches this session id"))
        );

        mockMvc.perform(post("/sessions/1001/end")
                .contentType("application/json")
                .content("""
                    {
                      "userId": "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb",
                      "moodAfter": 7
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.code").value("SESSION_NOT_FOUND"))
            .andExpect(jsonPath("$.error.details[0].field").value("sessionId"));
    }

    static class StubSessionService extends SessionService {
        List<SessionLibraryItemResponse> library = List.of();
        SessionDetailResponse detail;
        SessionRunResponse startResponse;
        SessionChatResponse chatResponse;
        SessionRunResponse endResponse;
        RuntimeException endException;

        StubSessionService() {
            super(null);
        }

        @Override
        public List<SessionLibraryItemResponse> getSessionLibrary() {
            return library;
        }

        @Override
        public SessionDetailResponse getSessionDetail(Long sessionId) {
            return detail;
        }

        @Override
        public SessionRunResponse startSession(Long sessionId, StartSessionRequest request) {
            return startResponse;
        }

        @Override
        public SessionChatResponse sendChatMessage(Long sessionId, SendChatMessageRequest request) {
            return chatResponse;
        }

        @Override
        public SessionRunResponse endSession(Long sessionId, EndSessionRequest request) {
            if (endException != null) {
                throw endException;
            }
            return endResponse;
        }
    }
}
