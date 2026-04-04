package com.SE320.therapy.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SE320.therapy.controller.UserController;

class MenuTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUpOutputCapture() {
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    void execute_printsMainMenu() {
        Menu menu = createMenu("7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Main Menu ==="));
        assertTrue(output.contains("1. Authentication"));
        assertTrue(output.contains("7. Exit"));
        assertTrue(output.contains("Select an area: "));
    }

    @Test
    void execute_routesToAuthenticationCommands() {
        CountingUserCommands userCommands = new CountingUserCommands();
        Menu menu = createMenu(userCommands, new CountingSessionCommands(), new CountingDiaryCommands(),
                new CountingDashboardCommands(), "auth\n7\n");

        menu.execute();

        assertEquals(1, userCommands.executeCalls);
    }

    @Test
    void execute_routesToSessionCommands() {
        CountingSessionCommands sessionCommands = new CountingSessionCommands();
        Menu menu = createMenu(new CountingUserCommands(), sessionCommands, new CountingDiaryCommands(),
                new CountingDashboardCommands(), "session\n7\n");

        menu.execute();

        assertEquals(1, sessionCommands.executeCalls);
    }

    @Test
    void execute_routesToDiaryCommands() {
        CountingDiaryCommands diaryCommands = new CountingDiaryCommands();
        Menu menu = createMenu(new CountingUserCommands(), new CountingSessionCommands(), diaryCommands,
                new CountingDashboardCommands(), "3\n7\n");

        menu.execute();

        assertEquals(1, diaryCommands.executeCalls);
    }

    @Test
    void execute_handlesDashboardAndStaticOptions() {
        CountingDashboardCommands dashboardCommands = new CountingDashboardCommands();
        Menu menu = createMenu(new CountingUserCommands(), new CountingSessionCommands(), new CountingDiaryCommands(),
                dashboardCommands, "dashboard\ncrisis\nsettings\nbad-option\nhelp\nexit\n");

        menu.execute();

        String output = getOutput();
        assertEquals(1, dashboardCommands.executeCalls);
        assertTrue(output.contains("Crisis commands are not available yet."));
        assertTrue(output.contains("Settings commands are not available yet."));
        assertTrue(output.contains("Please choose a valid menu option."));
        assertEquals(2, countOccurrences(output, "=== Main Menu ==="));
    }

    private Menu createMenu(String input) {
        return createMenu(new CountingUserCommands(), new CountingSessionCommands(), new CountingDiaryCommands(),
                new CountingDashboardCommands(), input);
    }

    private Menu createMenu(UserCommands userCommands,
                            SessionCommands sessionCommands,
                            DiaryCommands diaryCommands,
                            DashboardCommands dashboardCommands,
                            String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Menu(scanner, userCommands, sessionCommands, diaryCommands, dashboardCommands);
    }

    private String getOutput() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private int countOccurrences(String value, String target) {
        return value.split(java.util.regex.Pattern.quote(target), -1).length - 1;
    }

    private static final class CountingUserCommands extends UserCommands {
        private int executeCalls;

        private CountingUserCommands() {
            super((UserController) null, new Scanner(new ByteArrayInputStream(new byte[0])));
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }

    private static final class CountingSessionCommands extends SessionCommands {
        private int executeCalls;

        private CountingSessionCommands() {
            super(null, new Scanner(new ByteArrayInputStream(new byte[0])), new CountingUserCommands());
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }

    private static final class CountingDiaryCommands extends DiaryCommands {
        private int executeCalls;

        private CountingDiaryCommands() {
            super(new Scanner(new ByteArrayInputStream(new byte[0])), null, null);
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }

    private static final class CountingDashboardCommands extends DashboardCommands {
        private int executeCalls;

        private CountingDashboardCommands() {
            super(null, new CountingUserCommands(), new Scanner(new ByteArrayInputStream(new byte[0])));
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }
}
