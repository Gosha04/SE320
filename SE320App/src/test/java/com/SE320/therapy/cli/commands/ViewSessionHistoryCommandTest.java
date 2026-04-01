package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ViewSessionHistoryCommandTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = mock(SessionController.class);
    }

    private String runCommand(String userId) {
        ViewSessionHistoryCommand command = new ViewSessionHistoryCommand(sessionController, userId);

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
}