package com.SE320.therapy.cli.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

abstract class MenuTestSupport {

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

    protected Menu createMenu(String input) {
        return createMenu(createUserCommands("back\n"), createSessionCommands("back\n"), createDiaryCommands("back\n"),
                createDashboardCommands("back\n"), createCrisisCommands("back\n"), new SettingsCommands(), input);
    }

    protected Menu createMenu(UserCommands userCommands,
                              SessionCommands sessionCommands,
                              DiaryCommands diaryCommands,
                              DashboardCommands dashboardCommands,
                              CrisisCommands crisisCommands,
                              SettingsCommands settingsCommands,
                              String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Menu(scanner, userCommands, sessionCommands, diaryCommands, dashboardCommands, crisisCommands,
                settingsCommands);
    }

    protected String getOutput() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    protected int countOccurrences(String value, String target) {
        return value.split(java.util.regex.Pattern.quote(target), -1).length - 1;
    }

    protected UserCommands createUserCommands(String input) {
        return new UserCommands(null, scannerFrom(input));
    }

    protected SessionCommands createSessionCommands(String input) {
        return new SessionCommands(null, scannerFrom(input), createUserCommands("back\n"));
    }

    protected DiaryCommands createDiaryCommands(String input) {
        return new DiaryCommands(scannerFrom(input), null, () -> null);
    }

    protected DashboardCommands createDashboardCommands(String input) {
        return new DashboardCommands(null, createUserCommands("back\n"), scannerFrom(input));
    }

    protected CrisisCommands createCrisisCommands(String input) {
        return new CrisisCommands(null, createUserCommands("back\n"), scannerFrom(input));
    }

    protected Scanner scannerFrom(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }
}
