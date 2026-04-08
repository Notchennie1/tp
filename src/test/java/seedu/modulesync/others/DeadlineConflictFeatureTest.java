package seedu.modulesync.others;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.modulesync.command.CheckConflictsCommand;
import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.parser.Parser;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

class DeadlineConflictFeatureTest {

    static class TestStorage extends Storage {
        boolean saved;

        TestStorage(Path path) {
            super(path);
        }

        @Override
        public void save(ModuleBook moduleBook) {
            saved = true;
        }
    }

    @Test
    void parse_conflictCommands_returnsCheckConflictsCommand() throws ModuleSyncException {
        Parser parser = new Parser();
        assertTrue(parser.parse("check /conflicts") instanceof CheckConflictsCommand);
        assertTrue(parser.parse("/conflicts") instanceof CheckConflictsCommand);
    }

    @Test
    void execute_withSameDayDeadlines_printsConflictGroupsWithoutSaving(@TempDir Path tempDir)
            throws ModuleSyncException {
        ModuleBook moduleBook = new ModuleBook();
        moduleBook.getOrCreate("CS9999").addTodo("Reference task");
        moduleBook.getOrCreate("CS2100").addDeadline("Quiz",
                LocalDateTime.of(2026, 4, 15, 18, 0));
        moduleBook.getOrCreate("CS2100").addDeadline("Lab report",
                LocalDateTime.of(2026, 4, 16, 23, 59));
        moduleBook.getOrCreate("CS2113").addDeadline("Project checkpoint",
                LocalDateTime.of(2026, 4, 15, 9, 0));
        moduleBook.getOrCreate("CS2113").addDeadline("Demo prep",
                LocalDateTime.of(2026, 4, 16, 10, 30));
        moduleBook.getOrCreate("CS2040S").addDeadline("Archived draft",
                LocalDateTime.of(2026, 4, 16, 8, 0)).markDone();

        TestStorage storage = new TestStorage(tempDir.resolve("modules.txt"));
        Ui ui = new Ui(new java.util.Scanner(new ByteArrayInputStream(new byte[0])));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            new CheckConflictsCommand().execute(moduleBook, storage, ui);
        } finally {
            System.setOut(originalOut);
        }

        String actual = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        String expected = "Here are your same-day deadline conflicts:\n"
                + "2026-04-15 (2 deadlines)\n"
                + "  4.[CS2113] [D][ ] Project checkpoint (due: 09:00)\n"
                + "  2.[CS2100] [D][ ] Quiz (due: 18:00)\n"
                + "2026-04-16 (2 deadlines)\n"
                + "  5.[CS2113] [D][ ] Demo prep (due: 10:30)\n"
                + "  3.[CS2100] [D][ ] Lab report (due: 23:59)\n";

        assertEquals(expected, actual);
        assertFalse(storage.saved);
    }

    @Test
    void execute_withoutSameDayDeadlines_printsNoConflictMessage(@TempDir Path tempDir)
            throws ModuleSyncException {
        ModuleBook moduleBook = new ModuleBook();
        moduleBook.getOrCreate("CS2113").addDeadline("Project checkpoint",
                LocalDateTime.of(2026, 4, 15, 9, 0));
        moduleBook.getOrCreate("CS2100").addDeadline("Quiz",
                LocalDateTime.of(2026, 4, 16, 18, 0));

        TestStorage storage = new TestStorage(tempDir.resolve("modules.txt"));
        Ui ui = new Ui(new java.util.Scanner(new ByteArrayInputStream(new byte[0])));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            new CheckConflictsCommand().execute(moduleBook, storage, ui);
        } finally {
            System.setOut(originalOut);
        }

        String actual = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertEquals("No same-day deadline conflicts found.\n", actual);
        assertFalse(storage.saved);
    }
}
