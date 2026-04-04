package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
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
        sessionController.library = List.of("Thought Record", "Behavioral Activation", "Cognitive Restructuring");

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
        sessionController.library = List.of("Thought Record", "Behavioral Activation", "Cognitive Restructuring");
        sessionController.sessionToStart = buildSession(1L, "user1", "Thought Record", SessionStatus.ACTIVE);

        String output = runCommands("2\n1\n6\n", "user1");

        assertTrue(output.contains("--- Start New Session ---"));
        assertTrue(output.contains("New CBT session started successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));
        assertEquals(1, sessionController.viewSessionLibraryCalls);
        assertEquals("user1", sessionController.lastStartedUserId);
        assertEquals("Thought Record", sessionController.lastStartedSessionType);
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
        sessionController.sessionToContinue = buildSession(1L, "user1", "Thought Record", SessionStatus.ACTIVE);

        String output = runCommands("3\n1\n6\n", "user1");

        assertTrue(output.contains("Enter session ID to continue:"));
        assertTrue(output.contains("Session continued successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));
        assertEquals("user1", sessionController.lastContinuedUserId);
        assertEquals(1L, sessionController.lastContinuedSessionId);
    }

    @Test
    void execute_endsSession_whenValidSessionIdIsEntered() {
        sessionController.history = List.of(buildSession(1L, "user1", "Thought Record", SessionStatus.ACTIVE));

        String output = runCommands("4\n1\n6\n", "user1");

        assertTrue(output.contains("--- Available Sessions ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));
        assertTrue(output.contains("Session ended successfully."));
        assertEquals(1, sessionController.viewSessionHistoryCalls);
        assertEquals("user1", sessionController.lastEndedUserId);
        assertEquals(1L, sessionController.lastEndedSessionId);
    }

    @Test
    void execute_displaysSessionHistory_whenSessionsExist() {
        CBTSession session = buildSession(1L, "user1", "Thought Record", SessionStatus.ENDED);
        session.setStartedAt(LocalDateTime.now().minusMinutes(15));
        session.setEndedAt(LocalDateTime.now());
        sessionController.history = List.of(session);

        String output = runCommands("5\n6\n", "user1");

        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ENDED"));
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
        private List<String> library = List.of();
        private List<CBTSession> history = List.of();
        private CBTSession sessionToStart;
        private CBTSession sessionToContinue;
        private int viewSessionLibraryCalls;
        private int viewSessionHistoryCalls;
        private String lastStartedUserId;
        private String lastStartedSessionType;
        private String lastContinuedUserId;
        private Long lastContinuedSessionId;
        private String lastEndedUserId;
        private Long lastEndedSessionId;

        @Override
        public List<String> viewSessionLibrary() {
            viewSessionLibraryCalls++;
            return library;
        }

        @Override
        public CBTSession startNewSession(String userId, String sessionType) {
            lastStartedUserId = userId;
            lastStartedSessionType = sessionType;
            return sessionToStart;
        }

        @Override
        public List<CBTSession> viewSessionHistory(String userId) {
            viewSessionHistoryCalls++;
            return history;
        }

        @Override
        public CBTSession continueSession(String userId, Long sessionId) {
            lastContinuedUserId = userId;
            lastContinuedSessionId = sessionId;
            return sessionToContinue;
        }

        @Override
        public void endSession(String userId, Long sessionId) {
            lastEndedUserId = userId;
            lastEndedSessionId = sessionId;
        }
    }
}
