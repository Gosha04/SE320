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
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EndSessionCommandTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = mock(SessionController.class);

        CBTSession activeSession = new CBTSession();
        activeSession.setSessionId(1L);
        activeSession.setUserId("user1");
        activeSession.setSessionType("Thought Record");
        activeSession.setStatus(SessionStatus.ACTIVE);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(activeSession));
        when(sessionController.viewSessionHistory("otherUser")).thenReturn(Collections.emptyList());

        doThrow(new IllegalArgumentException("Session not found."))
                .when(sessionController).endSession("user1", 99L);

        doThrow(new IllegalStateException("This session has already ended."))
                .when(sessionController).endSession("user1", 1L);
    }

    private String runCommand(String input, String userId) {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        EndSessionCommand command = new EndSessionCommand(sessionController, scanner, userCommandsFor(userId));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            command.execute();
        } finally {
            System.setOut(originalOut);
            scanner.close();
        }

        return outputStream.toString();
    }

    private UserCommands userCommandsFor(String userId) {
        return new StubUserCommands(userId);
    }

    @Test
    void execute_endsSession_whenValidSessionIdIsEntered() {
        reset(sessionController);

        CBTSession activeSession = new CBTSession();
        activeSession.setSessionId(1L);
        activeSession.setUserId("user1");
        activeSession.setSessionType("Thought Record");
        activeSession.setStatus(SessionStatus.ACTIVE);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(activeSession));

        String output = runCommand("1\n", "user1");

        assertTrue(output.contains("Session ended successfully."));
        verify(sessionController).endSession("user1", 1L);
    }

    @Test
    void execute_showsMessage_whenInputIsBlank() {
        reset(sessionController);

        CBTSession activeSession = new CBTSession();
        activeSession.setSessionId(1L);
        activeSession.setUserId("user1");
        activeSession.setSessionType("Thought Record");
        activeSession.setStatus(SessionStatus.ACTIVE);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(activeSession));

        String output = runCommand("\n", "user1");

        assertTrue(output.contains("Session ID cannot be empty."));
        verify(sessionController, never()).endSession(anyString(), anyLong());
    }

    @Test
    void execute_showsMessage_whenInputIsNotANumber() {
        reset(sessionController);

        CBTSession activeSession = new CBTSession();
        activeSession.setSessionId(1L);
        activeSession.setUserId("user1");
        activeSession.setSessionType("Thought Record");
        activeSession.setStatus(SessionStatus.ACTIVE);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(activeSession));

        String output = runCommand("abc\n", "user1");

        assertTrue(output.contains("Session ID must be a number."));
        verify(sessionController, never()).endSession(anyString(), anyLong());
    }

    @Test
    void execute_showsMessage_whenSessionDoesNotExist() {
        reset(sessionController);

        CBTSession activeSession = new CBTSession();
        activeSession.setSessionId(1L);
        activeSession.setUserId("user1");
        activeSession.setSessionType("Thought Record");
        activeSession.setStatus(SessionStatus.ACTIVE);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(activeSession));
        doThrow(new IllegalArgumentException("Session not found."))
                .when(sessionController).endSession("user1", 99L);

        String output = runCommand("99\n", "user1");

        assertTrue(output.contains("Session not found."));
    }

    @Test
    void execute_showsMessage_whenSessionBelongsToDifferentUser() {
        reset(sessionController);
        when(sessionController.viewSessionHistory("otherUser")).thenReturn(Collections.emptyList());

        String output = runCommand("1\n", "otherUser");

        assertTrue(output.contains("No session history found."));
    }

    @Test
    void execute_showsMessage_whenSessionAlreadyEnded() {
        reset(sessionController);

        CBTSession endedSession = new CBTSession();
        endedSession.setSessionId(1L);
        endedSession.setUserId("user1");
        endedSession.setSessionType("Thought Record");
        endedSession.setStatus(SessionStatus.ENDED);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(endedSession));
        doThrow(new IllegalStateException("This session has already ended."))
                .when(sessionController).endSession("user1", 1L);

        String output = runCommand("1\n", "user1");

        assertTrue(output.contains("This session has already ended."));
    }

    @Test
    void execute_requiresAuthenticatedUser() {
        String output = runCommand("1\n", null);

        assertTrue(output.contains("You must be logged in to end a session."));
        verify(sessionController, never()).viewSessionHistory(anyString());
        verify(sessionController, never()).endSession(anyString(), anyLong());
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
}
