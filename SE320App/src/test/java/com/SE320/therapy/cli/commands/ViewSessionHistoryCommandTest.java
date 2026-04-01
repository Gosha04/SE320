package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ViewSessionHistoryCommandTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        SessionService sessionService = new SessionService();
        sessionController = new SessionController(sessionService);
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
        String output = runCommand("user1");

        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("No session history found."));
    }

    @Test
    void execute_displaysSessionHistory_whenSessionsExist() {
        sessionController.startNewSession("user1", "Thought Record");
        sessionController.endSession("user1", 1L);

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
        String output = runCommand("");

        assertTrue(output.contains("User ID is required."));
    }
}