package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.grade.GradeProgressCalculator;
import seedu.modulesync.grade.GradeProgressSummary;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.semester.SemesterBook;
import seedu.modulesync.storage.SemesterStorage;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Command to display the student's grade summary and cumulative progress across semesters.
 */
public class ListGradesCommand extends SemesterCommand {

    private final GradeProgressCalculator gradeProgressCalculator;

    public ListGradesCommand(SemesterBook semesterBook, SemesterStorage semesterStorage) {
        super(semesterBook, semesterStorage);
        this.gradeProgressCalculator = new GradeProgressCalculator();
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        GradeProgressSummary summary = gradeProgressCalculator.calculateSummary(semesterBook);
        ui.showGradeProgressSummary(summary);
    }
}
