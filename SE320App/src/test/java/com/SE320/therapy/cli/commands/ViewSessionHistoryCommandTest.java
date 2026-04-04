package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.objects.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ViewSessionHistoryCommandTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = mock(SessionController.class);
    }

    private String runCommand(String userId) {
        ViewSessionHistoryCommand command = new ViewSessionHistoryCommand(sessionController, userCommandsFor(userId));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            command.execute();
        } finally {
            System.setOut(originalOut);
        }

        return outputStream.toString();
    }

    private UserCommands userCommandsFor(String userId) {
        return new StubUserCommands(userId);
    }

    @Test
    void execute_displaysMessage_whenNoSessionHistoryExists() {
        when(sessionController.viewSessionHistory("user1")).thenReturn(Collections.emptyList());

        String output = runCommand("user1");

        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("No session history found."));
    }

    @Test
    void execute_displaysSessionHistory_whenSessionsExist() {
        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ENDED);
        session.setStartedAt(LocalDateTime.now().minusMinutes(15));
        session.setEndedAt(LocalDateTime.now());

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(session));

        String output = runCommand("user1");

        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ENDED"));
        assertTrue(output.contains("Started: "));
        assertTrue(output.contains("Ended: "));
        assertTrue(output.contains("-------------------------"));
    }

    @Test
    void execute_showsError_whenUserIdIsInvalid() {
        when(sessionController.viewSessionHistory(""))
                .thenThrow(new IllegalArgumentException("User ID is required."));

        String output = runCommand("");

        assertTrue(output.contains("User ID is required."));
    }

    @Test
    void execute_requiresAuthenticatedUser() {
        String output = runCommand(null);

        assertTrue(output.contains("You must be logged in to view session history."));
        verify(sessionController, never()).viewSessionHistory(anyString());
    }

    private static final class StubUserCommands extends UserCommands {
        private final String userId;

        private StubUserCommands(String userId) {
            super(null, new Scanner(new java.io.ByteArrayInputStream(new byte[0])));
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
