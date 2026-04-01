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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartNewSessionCommandTest {

    private SessionService sessionService;
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
        sessionController = new SessionController(sessionService);
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

        assertEquals(1, sessionService.viewSessionHistory("user1").size());
    }

    @Test
    void execute_showsMessage_whenInputIsBlank() {
        String output = runCommand("\n", "user1");

        assertTrue(output.contains("Please enter a session number."));
    }

    @Test
    void execute_showsMessage_whenInputIsNotANumber() {
        String output = runCommand("abc\n", "user1");

        assertTrue(output.contains("Session choice must be a valid number."));
    }

    @Test
    void execute_showsMessage_whenChoiceIsOutOfRange() {
        String output = runCommand("99\n", "user1");

        assertTrue(output.contains("Please choose a number from the session library."));
    }
}