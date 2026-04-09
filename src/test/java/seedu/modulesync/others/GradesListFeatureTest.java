package seedu.modulesync.others;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.modulesync.command.ListGradesCommand;
import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.Module;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.parser.Parser;
import seedu.modulesync.semester.Semester;
import seedu.modulesync.semester.SemesterBook;
import seedu.modulesync.storage.SemesterStorage;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

class GradesListFeatureTest {

    private static final String NO_GRADE_SUMMARY_MESSAGE =
            "No graded modules found. A grade summary cannot be generated yet.\n";

    static class StorageStub extends Storage {
        private boolean isSaved;

        StorageStub(Path path) {
            super(path);
        }

        @Override
        public void save(ModuleBook moduleBook) {
            isSaved = true;
        }

        boolean isSaved() {
            return isSaved;
        }
    }

    @Test
    void parse_gradesList_returnsListGradesCommand(@TempDir Path tempDir) throws ModuleSyncException {
        SemesterBook semesterBook = new SemesterBook();
        SemesterStorage semesterStorage = new SemesterStorage(tempDir);
        Parser parser = new Parser(semesterBook, semesterStorage);

        assertTrue(parser.parse("grades list") instanceof ListGradesCommand);
    }

    @Test
    void execute_multipleSemestersWithRecordedGrades_printsGradeSummaryWithCumulativeProgress(@TempDir Path tempDir)
            throws ModuleSyncException {
        SemesterBook semesterBook = createSemesterBookWithGrades();
        SemesterStorage semesterStorage = new SemesterStorage(tempDir);
        ModuleBook activeModuleBook = semesterBook.getCurrentModuleBook();
        StorageStub storageStub = new StorageStub(tempDir.resolve("AY2526-S1.txt"));
        Ui ui = new Ui(new java.util.Scanner(new ByteArrayInputStream(new byte[0])));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            new ListGradesCommand(semesterBook, semesterStorage).execute(activeModuleBook, storageStub, ui);
        } finally {
            System.setOut(originalOut);
        }

        String actual = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        String expected = "AY2525-S2 Results (Archived)\n"
                + "Module   Credits  Grade   Points\n"
                + "CS1010   4        A       5.0\n"
                + "GEA1000  4        S       N/A\n"
                + "Semester CAP: 5.00 (4 MCs)\n"
                + "Cumulative CAP: 5.00 (4 MCs across 1 semester)\n"
                + "\n"
                + "AY2526-S1 Results (Current)\n"
                + "Module   Credits  Grade   Points\n"
                + "CS2113   4        A+      5.0\n"
                + "MA2001   4        B+      4.0\n"
                + "Semester CAP: 4.50 (8 MCs)\n"
                + "Cumulative CAP: 4.67 (12 MCs across 2 semesters)\n";

        assertEquals(expected, actual);
        assertFalse(storageStub.isSaved());
    }

    @Test
    void execute_withoutRecordedGrades_printsNoGradeSummaryMessage(@TempDir Path tempDir) throws ModuleSyncException {
        SemesterBook semesterBook = new SemesterBook();
        Semester emptySemester = new Semester("AY2526-S2", false);
        semesterBook.addSemester(emptySemester);
        semesterBook.setCurrentSemester("AY2526-S2");

        SemesterStorage semesterStorage = new SemesterStorage(tempDir);
        ModuleBook activeModuleBook = semesterBook.getCurrentModuleBook();
        StorageStub storageStub = new StorageStub(tempDir.resolve("AY2526-S2.txt"));
        Ui ui = new Ui(new java.util.Scanner(new ByteArrayInputStream(new byte[0])));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            new ListGradesCommand(semesterBook, semesterStorage).execute(activeModuleBook, storageStub, ui);
        } finally {
            System.setOut(originalOut);
        }

        String actual = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertEquals(NO_GRADE_SUMMARY_MESSAGE, actual);
        assertFalse(storageStub.isSaved());
    }

    /**
     * Creates a two-semester grade history for the grade-summary feature test.
     *
     * @return the populated semester book
     * @throws ModuleSyncException if the current semester cannot be set
     */
    private SemesterBook createSemesterBookWithGrades() throws ModuleSyncException {
        SemesterBook semesterBook = new SemesterBook();
        Semester archivedSemester = new Semester("AY2525-S2", true);
        Semester currentSemester = new Semester("AY2526-S1", false);

        assignGrade(archivedSemester, "CS1010", "A", 4);
        assignGrade(archivedSemester, "GEA1000", "S", 4);
        assignGrade(currentSemester, "CS2113", "A+", 4);
        assignGrade(currentSemester, "MA2001", "B+", 4);

        semesterBook.addSemester(archivedSemester);
        semesterBook.addSemester(currentSemester);
        semesterBook.setCurrentSemester("AY2526-S1");
        return semesterBook;
    }

    /**
     * Assigns a recorded grade and credits to one module in the given semester.
     *
     * @param semester the semester containing the module
     * @param moduleCode the module code to update
     * @param grade the recorded grade
     * @param credits the module credits
     */
    private void assignGrade(Semester semester, String moduleCode, String grade, int credits) {
        Module module = semester.getModuleBook().getOrCreate(moduleCode);
        module.setGrade(grade);
        module.setCredits(credits);
    }
}
