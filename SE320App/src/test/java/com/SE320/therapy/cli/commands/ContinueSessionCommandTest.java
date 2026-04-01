package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContinueSessionCommandTest {

    private SessionService sessionService;
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
        sessionController = new SessionController(sessionService);

        sessionController.startNewSession("user1", "Thought Record");
    }

    private String runCommand(String input, String userId) {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ContinueSessionCommand command = new ContinueSessionCommand(sessionController, scanner, userId);

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
    void execute_continuesSession_whenValidSessionIdIsEntered() {
        String output = runCommand("1\n", "user1");

        assertTrue(output.contains("Session continued successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));
    }

    @Test
    void execute_showsMessage_whenInputIsBlank() {
        String output = runCommand("\n", "user1");

        assertTrue(output.contains("Session ID cannot be empty."));
    }

    @Test
    void execute_showsMessage_whenInputIsNotANumber() {
        String output = runCommand("abc\n", "user1");

        assertTrue(output.contains("Session ID must be a valid number."));
    }

    @Test
    void execute_showsMessage_whenSessionDoesNotExist() {
        String output = runCommand("99\n", "user1");

        assertTrue(output.contains("Session not found."));
    }

    @Test
    void execute_showsMessage_whenSessionBelongsToDifferentUser() {
        String output = runCommand("1\n", "otherUser");

        assertTrue(output.contains("Session not found."));
    }
}