package com.SE320.therapy.cli.commands;

import com.SE320.therapy.controller.SessionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ViewSessionLibraryCommandTest {

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionController = mock(SessionController.class);

        when(sessionController.viewSessionLibrary()).thenReturn(List.of(
                "Thought Record",
                "Behavioral Activation",
                "Cognitive Restructuring"
        ));
    }

    private String runCommand() {
        ViewSessionLibraryCommand command = new ViewSessionLibraryCommand(sessionController);

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
    void execute_displaysSessionLibrary() {
        String output = runCommand();

        assertTrue(output.contains("--- Session Library ---"));
        assertTrue(output.contains("1. Thought Record"));
        assertTrue(output.contains("2. Behavioral Activation"));
        assertTrue(output.contains("3. Cognitive Restructuring"));
    }
}