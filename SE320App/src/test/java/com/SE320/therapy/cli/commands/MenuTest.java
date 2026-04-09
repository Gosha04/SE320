package com.SE320.therapy.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MenuTest extends MenuTestSupport {

    @Test
    void execute_printsMainMenu() {
        Menu menu = createMenu("7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Main Menu ==="));
        assertTrue(output.contains("1. Authentication"));
        assertTrue(output.contains("5. Crisis"));
        assertTrue(output.contains("6. Settings"));
        assertTrue(output.contains("7. Exit"));
        assertTrue(output.contains("Select an area: "));
    }

    @Test
    void execute_routesToAuthenticationCommands() {
        Menu menu = createMenu(createUserCommands("back\n"), createSessionCommands("back\n"), createDiaryCommands("back\n"),
                createDashboardCommands("back\n"), createCrisisCommands("back\n"), new SettingsCommands(), "auth\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== User Menu ==="));
    }

    @Test
    void execute_routesToSessionCommands() {
        Menu menu = createMenu(createUserCommands("back\n"), createSessionCommands("back\n"), createDiaryCommands("back\n"),
                createDashboardCommands("back\n"), createCrisisCommands("back\n"), new SettingsCommands(), "session\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Session Menu ==="));
    }

    @Test
    void execute_routesToDiaryCommands() {
        Menu menu = createMenu(createUserCommands("back\n"), createSessionCommands("back\n"), createDiaryCommands("back\n"),
                createDashboardCommands("back\n"), createCrisisCommands("back\n"), new SettingsCommands(), "3\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Diary Menu ==="));
    }

    @Test
    void execute_routesToSettingsCommands() {
        Menu menu = createMenu(createUserCommands("back\n"), createSessionCommands("back\n"), createDiaryCommands("back\n"),
                createDashboardCommands("back\n"), createCrisisCommands("back\n"), new SettingsCommands(), "settings\n7\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("We don't have any settings for the CLI"));
    }

    @Test
    void execute_handlesDashboardAndStaticOptions() {
        Menu menu = createMenu(createUserCommands("back\n"), createSessionCommands("back\n"), createDiaryCommands("back\n"),
                createDashboardCommands("back\n"), createCrisisCommands("back\n"), new SettingsCommands(),
                "dashboard\ncrisis\nsettings\nbad-option\nhelp\nexit\n");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Dashboard Menu ==="));
        assertTrue(output.contains("=== Crisis Menu ==="));
        assertTrue(output.contains("We don't have any settings for the CLI"));
        assertTrue(output.contains("Please choose a valid menu option."));
        assertEquals(2, countOccurrences(output, "=== Main Menu ==="));
    }
}
