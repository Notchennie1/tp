package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.semester.Semester;
import seedu.modulesync.semester.SemesterBook;
import seedu.modulesync.storage.SemesterStorage;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Command to switch the CLI view to an existing semester.
 */
public class SwitchSemesterCommand extends SemesterCommand {

    private final String targetSemesterName;

    /**
     * Constructs a command that switches to the given semester.
     *
     * @param semesterBook the shared semester book
     * @param semesterStorage the shared semester storage
     * @param targetSemesterName the semester name to switch to
     */
    public SwitchSemesterCommand(SemesterBook semesterBook, SemesterStorage semesterStorage,
                                 String targetSemesterName) {
        super(semesterBook, semesterStorage);
        assert targetSemesterName != null && !targetSemesterName.isBlank()
                : "Target semester name must not be blank";
        this.targetSemesterName = targetSemesterName.trim();
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        String previousSemesterName = semesterBook.getCurrentSemesterName();
        semesterBook.setCurrentSemester(targetSemesterName);
        semesterStorage.save(semesterBook);

        Semester currentSemester = semesterBook.getCurrentSemester();
        ui.showSemesterViewChanged(
                currentSemester.getName(),
                currentSemester.isArchived(),
                previousSemesterName);
    }
}
