package com.SE320.therapy.cli;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.objects.SessionStatus;
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

public class CbtSessionMenuTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = mock(SessionController.class);
    }

    private String runMenu(String input, String userId) {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        CbtSessionMenu menu = new CbtSessionMenu(sessionController, scanner, userId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            menu.display();
        } finally {
            System.setOut(originalOut);
            scanner.close();
        }

        return outputStream.toString();
    }

    @Test
    void display_executesViewSessionLibraryCommand() {
        when(sessionController.viewSessionLibrary()).thenReturn(List.of(
                "Thought Record",
                "Behavioral Activation",
                "Cognitive Restructuring"));

        String output = runMenu("1\n6\n", "user1");

        assertTrue(output.contains("--- CBT Sessions ---"));
        assertTrue(output.contains("--- Session Library ---"));
        assertTrue(output.contains("1. Thought Record"));
        assertTrue(output.contains("2. Behavioral Activation"));
        assertTrue(output.contains("3. Cognitive Restructuring"));

        verify(sessionController).viewSessionLibrary();
    }

    @Test
    void display_executesStartNewSessionCommand() {
        when(sessionController.viewSessionLibrary()).thenReturn(List.of(
                "Thought Record",
                "Behavioral Activation",
                "Cognitive Restructuring"));

        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ACTIVE);

        when(sessionController.startNewSession("user1", "Thought Record")).thenReturn(session);

        String output = runMenu("2\n1\n6\n", "user1");

        assertTrue(output.contains("--- Start New Session ---"));
        assertTrue(output.contains("New CBT session started successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));

        verify(sessionController).viewSessionLibrary();
        verify(sessionController).startNewSession("user1", "Thought Record");
    }

    @Test
    void display_executesContinueSessionCommand() {
        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ACTIVE);

        when(sessionController.continueSession("user1", 1L)).thenReturn(session);

        String output = runMenu("3\n1\n6\n", "user1");

        assertTrue(output.contains("Enter session ID to continue:"));
        assertTrue(output.contains("Session continued successfully."));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));

        verify(sessionController).continueSession("user1", 1L);
    }

    @Test
    void display_executesEndSessionCommand() {
        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ACTIVE);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(session));

        String output = runMenu("4\n1\n6\n", "user1");

        assertTrue(output.contains("--- Available Sessions ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ACTIVE"));
        assertTrue(output.contains("Session ended successfully."));

        verify(sessionController).viewSessionHistory("user1");
        verify(sessionController).endSession("user1", 1L);
    }

    @Test
    void display_executesViewSessionHistoryCommand() {
        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ENDED);

        when(sessionController.viewSessionHistory("user1")).thenReturn(List.of(session));

        String output = runMenu("5\n6\n", "user1");

        assertTrue(output.contains("--- Session History ---"));
        assertTrue(output.contains("Session ID: 1"));
        assertTrue(output.contains("Type: Thought Record"));
        assertTrue(output.contains("Status: ENDED"));

        verify(sessionController).viewSessionHistory("user1");
    }

    @Test
    void display_showsErrorForInvalidChoice() {
        String output = runMenu("99\n6\n", "user1");

        assertTrue(output.contains("Invalid choice. Please enter 1, 2, 3, 4, 5, or 6."));
        verifyNoInteractions(sessionController);
    }
}