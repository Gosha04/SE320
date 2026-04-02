package com.SE320.therapy.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.SE320.therapy.controller.SessionController;
import com.SE320.therapy.controller.UserController;
import com.SE320.therapy.service.SessionService;

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
    void rintsMainMenu() {
        Menu menu = createMenu("7");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("=== Main Menu ==="));
        assertTrue(output.contains("1. Authentication"));
        assertTrue(output.contains("7. Exit"));
        assertTrue(output.contains("Select an area: "));
    }

    @Test
    void shouldPrintMenu() {
        CountingUserCommands userCommands = new CountingUserCommands();
        Menu menu = createMenu(userCommands, new RecordingSessionController(), new CountingNewDiaryEntryCommand(),
                new CountingViewDiaryEntriesCommand(), new CountingViewDiaryInsightsCommand(),
                new FixedUserIdSupplier(null), "auth", "exit");

        menu.execute();

        assertEquals(1, userCommands.executeCalls);
    }

    @Test // Should be removed eventually, [Josh] using for testing while others are busy
    void shouldHaveResponses() {
        Menu menu = createMenu("dashboard", "crisis", "settings", "bad-option", " HELP ", "Exit");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("Dashboard commands are not available yet."));
        assertTrue(output.contains("Crisis commands are not available yet."));
        assertTrue(output.contains("Settings commands are not available yet."));
        assertTrue(output.contains("Please choose a valid menu option."));
        assertEquals(2, countOccurrences(output, "=== Main Menu ==="));
    }

    @Test
    void shouldHandleInjections() {
        CountingNewDiaryEntryCommand newEntry = new CountingNewDiaryEntryCommand();
        CountingViewDiaryEntriesCommand viewEntries = new CountingViewDiaryEntriesCommand();
        CountingViewDiaryInsightsCommand viewInsights = new CountingViewDiaryInsightsCommand();
        Menu menu = createMenu(new CountingUserCommands(), new RecordingSessionController(), newEntry,
                viewEntries, viewInsights, new FixedUserIdSupplier(null), "3", "1", "2", "3", "4", "7");

        menu.execute();

        assertEquals(1, newEntry.executeCalls);
        assertEquals(1, viewEntries.executeCalls);
        assertEquals(1, viewInsights.executeCalls);
        assertTrue(getOutput().contains("=== Diary Menu ==="));
    }

    @Test
    void shouldHandleInvalid() {
        Menu menu = createMenu("diary", "unknown", "help", "back", "exit");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("Please choose a valid diary option."));
        assertEquals(2, countOccurrences(output, "=== Diary Menu ==="));
    }

    @Test
    void shouldDisplaySessions() {
        RecordingSessionController sessionController = new RecordingSessionController();
        sessionController.library = List.of("Thought Challenging", "Breathing Exercise");
        Menu menu = createMenu(new CountingUserCommands(), sessionController, new CountingNewDiaryEntryCommand(),
                new CountingViewDiaryEntriesCommand(), new CountingViewDiaryInsightsCommand(),
                new FixedUserIdSupplier(null), "session", "library", "back", "exit");

        menu.execute();

        assertEquals(1, sessionController.viewSessionLibraryCalls);
        String output = getOutput();
        assertTrue(output.contains("=== Session Menu ==="));
        assertTrue(output.contains("--- Session Library ---"));
        assertTrue(output.contains("1. Thought Challenging"));
        assertTrue(output.contains("2. Breathing Exercise"));
    }

    @Test
    void userRequiredForSessionStart() {
        FixedUserIdSupplier currentUserIdSupplier = new FixedUserIdSupplier(null);
        Menu menu = createMenu(new CountingUserCommands(), new RecordingSessionController(),
                new CountingNewDiaryEntryCommand(), new CountingViewDiaryEntriesCommand(),
                new CountingViewDiaryInsightsCommand(), currentUserIdSupplier, "session", "start", "back", "exit");

        menu.execute();

        assertEquals(1, currentUserIdSupplier.calls);
        assertTrue(getOutput().contains("You must be logged in to start a session."));
    }

    @Test
    void userRequiredForSessionView() {
        FixedUserIdSupplier currentUserIdSupplier = new FixedUserIdSupplier(null);
        Menu menu = createMenu(new CountingUserCommands(), new RecordingSessionController(),
                new CountingNewDiaryEntryCommand(), new CountingViewDiaryEntriesCommand(),
                new CountingViewDiaryInsightsCommand(), currentUserIdSupplier, "session", "history", "back", "exit");

        menu.execute();

        assertEquals(1, currentUserIdSupplier.calls);
        assertTrue(getOutput().contains("You must be logged in to view session history."));
    }

    @Test
    void shouldHandleInvalidSession() {
        RecordingSessionController sessionController = new RecordingSessionController();
        Menu menu = createMenu(new CountingUserCommands(), sessionController, new CountingNewDiaryEntryCommand(),
                new CountingViewDiaryEntriesCommand(), new CountingViewDiaryInsightsCommand(),
                new FixedUserIdSupplier(null), "2", "invalid", "help", "4", "7");

        menu.execute();

        String output = getOutput();
        assertTrue(output.contains("Please choose a valid session option."));
        assertEquals(2, countOccurrences(output, "=== Session Menu ==="));
        assertEquals(0, sessionController.viewSessionLibraryCalls);
    }

    private Menu createMenu(String... lines) {
        return createMenu(
                new CountingUserCommands(),
                new RecordingSessionController(),
                new CountingNewDiaryEntryCommand(),
                new CountingViewDiaryEntriesCommand(),
                new CountingViewDiaryInsightsCommand(),
                new FixedUserIdSupplier(null),
                lines);
    }

    // Assorted helpers for test state
    private Menu createMenu(
            UserCommands userCommands,
            SessionController sessionController,
            NewDiaryEntryCommand newDiaryEntryCommand,
            ViewDiaryEntriesCommand viewDiaryEntriesCommand,
            ViewDiaryInsightsCommand viewDiaryInsightsCommand,
            Supplier<UUID> currentUserIdSupplier,
            String... lines) {
        String input = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        return new Menu(
                scanner,
                userCommands,
                sessionController,
                newDiaryEntryCommand,
                viewDiaryEntriesCommand,
                viewDiaryInsightsCommand,
                currentUserIdSupplier);
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

    private static final class CountingNewDiaryEntryCommand extends NewDiaryEntryCommand {
        private int executeCalls;

        private CountingNewDiaryEntryCommand() {
            super(null, new Scanner(new ByteArrayInputStream(new byte[0])), () -> null);
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }

    private static final class CountingViewDiaryEntriesCommand extends ViewDiaryEntriesCommand {
        private int executeCalls;

        private CountingViewDiaryEntriesCommand() {
            super(null, () -> null);
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }

    private static final class CountingViewDiaryInsightsCommand extends ViewDiaryInsightsCommand {
        private int executeCalls;

        private CountingViewDiaryInsightsCommand() {
            super(null, () -> null);
        }

        @Override
        public void execute() {
            executeCalls++;
        }
    }

    private static final class RecordingSessionController extends SessionController {
        private int viewSessionLibraryCalls;
        private List<String> library = List.of();

        private RecordingSessionController() {
            super(new SessionService());
        }

        @Override
        public List<String> viewSessionLibrary() {
            viewSessionLibraryCalls++;
            return library;
        }
    }

    private static final class FixedUserIdSupplier implements Supplier<UUID> {
        private final UUID userId;
        private int calls;

        private FixedUserIdSupplier(UUID userId) {
            this.userId = userId;
        }

        @Override
        public UUID get() {
            calls++;
            return userId;
        }
    }
}
