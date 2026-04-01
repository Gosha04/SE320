package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class StartNewSessionCommandTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = mock(SessionController.class);

        when(sessionController.viewSessionLibrary()).thenReturn(List.of(
                "Thought Record",
                "Behavioral Activation",
                "Cognitive Restructuring"
        ));

        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ACTIVE);

        when(sessionController.startNewSession("user1", "Thought Record")).thenReturn(session);
    }

    private String runCommand(String input, String userId) {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        StartNewSessionCommand command = new StartNewSessionCommand(sessionController, scanner, userId);

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

    @Test
    void execute_startsNewSession_whenValidChoiceIsEntered() {
        String output = runCommand("1\n", "user1");

        assertTrue(output.contains("New CBT session started successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));

        verify(sessionController).startNewSession("user1", "Thought Record");
    }

    @Test
    void execute_showsMessage_whenInputIsBlank() {
        String output = runCommand("\n", "user1");

        assertTrue(output.contains("Please enter a session number."));
        verify(sessionController, never()).startNewSession(anyString(), anyString());
    }

    @Test
    void execute_showsMessage_whenInputIsNotANumber() {
        String output = runCommand("abc\n", "user1");

        assertTrue(output.contains("Session choice must be a valid number."));
        verify(sessionController, never()).startNewSession(anyString(), anyString());
    }

    @Test
    void execute_showsMessage_whenChoiceIsOutOfRange() {
        String output = runCommand("99\n", "user1");

        assertTrue(output.contains("Please choose a number from the session library."));
        verify(sessionController, never()).startNewSession(anyString(), anyString());
    }
}