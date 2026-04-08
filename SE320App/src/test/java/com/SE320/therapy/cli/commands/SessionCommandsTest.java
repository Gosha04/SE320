package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.dto.ChatMessageResponse;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.objects.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionCommandsTest {

    private RecordingSessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = new RecordingSessionController();
    }

    private String runCommands(String input, String userId) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        SessionCommands commands = new SessionCommands(sessionController, scanner, userCommandsFor(userId));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            commands.execute();
        } finally {
            System.setOut(originalOut);
            scanner.close();
        }

        return outputStream.toString();
    }

    @Test
    void execute_viewsSessionLibrary() {
        sessionController.library = List.of(
            new SessionLibraryItemResponse(1001L, "Thought Record", "desc", 20, 1, List.of("COGNITIVE")),
            new SessionLibraryItemResponse(1002L, "Behavioral Activation", "desc", 20, 2, List.of("BEHAVIORAL")),
            new SessionLibraryItemResponse(1003L, "Cognitive Restructuring", "desc", 20, 3, List.of("COGNITIVE"))
        );

        String output = runCommands("1\n6\n", "user1");

        assertTrue(output.contains("=== Session Menu ==="));
        assertTrue(output.contains("--- Session Library ---"));
        assertTrue(output.contains("1. Thought Record"));
        assertTrue(output.contains("2. Behavioral Activation"));
        assertTrue(output.contains("3. Cognitive Restructuring"));
        assertEquals(1, sessionController.viewSessionLibraryCalls);
    }

    @Test
    void execute_startsNewSession_whenValidChoiceIsEntered() {
        sessionController.library = List.of(
            new SessionLibraryItemResponse(1001L, "Thought Record", "desc", 20, 1, List.of("COGNITIVE")),
            new SessionLibraryItemResponse(1002L, "Behavioral Activation", "desc", 20, 2, List.of("BEHAVIORAL")),
            new SessionLibraryItemResponse(1003L, "Cognitive Restructuring", "desc", 20, 3, List.of("COGNITIVE"))
        );
        sessionController.sessionToStart = buildRunResponse(1001L, "Thought Record", "IN_PROGRESS");
        sessionController.chatResponse = buildChatResponse(1001L, "Let's look at that thought together.");

        String output = runCommands("2\n1\nI keep assuming the worst.\npause\n6\n", "user1");

        assertTrue(output.contains("--- Start New Session ---"));
        assertTrue(output.contains("New CBT session started successfully."));
        assertTrue(output.contains("Session ID: 1001"));
        assertTrue(output.contains("Title: Thought Record"));
        assertTrue(output.contains("Status: IN_PROGRESS"));
        assertTrue(output.contains("--- CBT Session Chat ---"));
        assertTrue(output.contains("Assistant: Welcome to Thought Record. What would you like to focus on today?"));
        assertTrue(output.contains("Assistant: Let's look at that thought together."));
        assertTrue(output.contains("Session paused. You can continue it later from the session menu using its session ID."));
        assertEquals(1, sessionController.viewSessionLibraryCalls);
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), sessionController.lastStartedUserId);
        assertEquals(1001L, sessionController.lastStartedSessionId);
        assertEquals(1001L, sessionController.lastChattedSessionId);
        assertEquals("I keep assuming the worst.", sessionController.lastChatMessage);
    }

    @Test
    void execute_requiresAuthenticatedUserToStartSession() {
        String output = runCommands("2\n6\n", null);

        assertTrue(output.contains("You must be logged in to start a session."));
        assertEquals(0, sessionController.viewSessionLibraryCalls);
        assertNull(sessionController.lastStartedUserId);
    }

    @Test
    void execute_continuesSession_whenValidSessionIdIsEntered() {
        sessionController.sessionToContinue = buildRunResponse(1L, "Thought Record", "IN_PROGRESS");
        sessionController.chatResponse = buildChatResponse(1L, "What evidence supports that thought?");

        String output = runCommands("3\n1\nI'm still stuck.\npause\n6\n", "user1");

        assertTrue(output.contains("Enter session ID to continue:"));
        assertTrue(output.contains("Session continued successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Title: Thought Record"));
        assertTrue(output.contains("Status: IN_PROGRESS"));
        assertTrue(output.contains("Assistant: Welcome back to Thought Record. What would you like to work on next?"));
        assertTrue(output.contains("Assistant: What evidence supports that thought?"));
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), sessionController.lastContinuedUserId);
        assertEquals(1L, sessionController.lastContinuedSessionId);
        assertEquals(1L, sessionController.lastChattedSessionId);
        assertEquals("I'm still stuck.", sessionController.lastChatMessage);
    }

    @Test
    void execute_endsSession_whenValidSessionIdIsEntered() {
        sessionController.history = List.of(buildRunResponse(1L, "Thought Record", "IN_PROGRESS"));

        String output = runCommands("4\n1\n6\n", "user1");

        assertTrue(output.contains("--- Available Sessions ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Title: Thought Record"));
        assertTrue(output.contains("Status: IN_PROGRESS"));
        assertTrue(output.contains("Session ended successfully."));
        assertEquals(1, sessionController.viewSessionHistoryCalls);
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), sessionController.lastEndedUserId);
        assertEquals(1L, sessionController.lastEndedSessionId);
    }

    @Test
    void execute_endsSessionFromInteractiveChat_whenEndCommandIsEntered() {
        sessionController.library = List.of(
            new SessionLibraryItemResponse(1001L, "Thought Record", "desc", 20, 1, List.of("COGNITIVE"))
        );
        sessionController.sessionToStart = buildRunResponse(1001L, "Thought Record", "IN_PROGRESS");

        String output = runCommands("2\n1\nend\n6\n", "user1");

        assertTrue(output.contains("Session ended successfully."));
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), sessionController.lastEndedUserId);
        assertEquals(1001L, sessionController.lastEndedSessionId);
    }

    @Test
    void execute_displaysSessionHistory_whenSessionsExist() {
        SessionRunResponse session = new SessionRunResponse(
            UUID.randomUUID(),
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            1L,
            "Thought Record",
            "COMPLETED",
            4,
            6,
            LocalDateTime.now().minusMinutes(15),
            LocalDateTime.now()
        );
        sessionController.history = List.of(session);

        String output = runCommands("5\n6\n", "user1");

        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Title: Thought Record"));
        assertTrue(output.contains("Status: COMPLETED"));
        assertTrue(output.contains("Started: "));
        assertTrue(output.contains("Ended: "));
        assertEquals(1, sessionController.viewSessionHistoryCalls);
    }

    @Test
    void execute_rejectsInvalidSessionMenuOption() {
        String output = runCommands("99\n6\n", "user1");

        assertTrue(output.contains("Please choose a valid session option."));
    }

    @Test
    void execute_requiresAuthenticatedUserToViewHistory() {
        String output = runCommands("5\n6\n", null);

        assertTrue(output.contains("You must be logged in to view session history."));
        assertEquals(0, sessionController.viewSessionHistoryCalls);
    }

    @Test
    void execute_requiresAuthenticatedUserToContinueSession() {
        String output = runCommands("3\n6\n", null);

        assertTrue(output.contains("You must be logged in to continue a session."));
        assertNull(sessionController.lastContinuedUserId);
    }

    @Test
    void execute_requiresAuthenticatedUserToEndSession() {
        String output = runCommands("4\n6\n", null);

        assertTrue(output.contains("You must be logged in to end a session."));
        assertEquals(0, sessionController.viewSessionHistoryCalls);
        assertNull(sessionController.lastEndedUserId);
    }

    private UserCommands userCommandsFor(String userId) {
        return new StubUserCommands(userId);
    }

    private static CBTSession buildSession(Long sessionId, String userId, String sessionType, SessionStatus status) {
        CBTSession session = new CBTSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setSessionType(sessionType);
        session.setStatus(status);
        return session;
    }

    private static final class StubUserCommands extends UserCommands {
        private final String userId;

        private StubUserCommands(String userId) {
            super(null, new Scanner(new ByteArrayInputStream(new byte[0])));
            this.userId = userId;
        }

        @Override
        public UUID getCurrentUserId() {
            return userId != null ? UUID.fromString("11111111-1111-1111-1111-111111111111") : null;
        }

        @Override
        public String getCurrentUserIdAsString() {
            return userId;
        }
    }

    private static final class RecordingSessionController extends SessionController {
        private List<SessionLibraryItemResponse> library = List.of();
        private List<SessionRunResponse> history = List.of();
        private SessionRunResponse sessionToStart;
        private SessionRunResponse sessionToContinue;
        private int viewSessionLibraryCalls;
        private int viewSessionHistoryCalls;
        private UUID lastStartedUserId;
        private Long lastStartedSessionId;
        private UUID lastContinuedUserId;
        private Long lastContinuedSessionId;
        private UUID lastEndedUserId;
        private Long lastEndedSessionId;
        private SessionChatResponse chatResponse;
        private Long lastChattedSessionId;
        private String lastChatMessage;

        @Override
        public List<SessionLibraryItemResponse> getSessionLibrary() {
            viewSessionLibraryCalls++;
            return library;
        }

        @Override
        public SessionRunResponse startSession(Long sessionId, com.SE320.therapy.dto.StartSessionRequest request) {
            lastStartedUserId = request.userId();
            lastStartedSessionId = sessionId;
            return sessionToStart;
        }

        @Override
        public List<SessionRunResponse> getSessionHistory(UUID userId) {
            viewSessionHistoryCalls++;
            return history;
        }

        @Override
        public SessionRunResponse continueSession(UUID userId, Long sessionId) {
            lastContinuedUserId = userId;
            lastContinuedSessionId = sessionId;
            return sessionToContinue;
        }

        @Override
        public SessionRunResponse endActiveSession(Long sessionId, EndSessionRequest request) {
            lastEndedUserId = request.userId();
            lastEndedSessionId = sessionId;
            return buildRunResponse(sessionId, "Thought Record", "COMPLETED");
        }

        @Override
        public SessionChatResponse sendChatMessage(Long sessionId, SendChatMessageRequest request) {
            lastChattedSessionId = sessionId;
            lastChatMessage = request.message();
            return chatResponse;
        }
    }

    private static SessionRunResponse buildRunResponse(Long sessionId, String title, String status) {
        return new SessionRunResponse(
            UUID.randomUUID(),
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            sessionId,
            title,
            status,
            5,
            null,
            LocalDateTime.now(),
            null
        );
    }

    private static SessionChatResponse buildChatResponse(Long sessionId, String assistantReply) {
        return new SessionChatResponse(
            UUID.randomUUID(),
            sessionId,
            new ChatMessageResponse(UUID.randomUUID(), "USER", "ignored", "TEXT", LocalDateTime.now()),
            new ChatMessageResponse(UUID.randomUUID(), "ASSISTANT", assistantReply, "TEXT", LocalDateTime.now())
        );
    }
}
