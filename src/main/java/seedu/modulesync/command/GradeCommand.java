package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.Module;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Command to record a final grade or S/U status for a module.
 * Stores the grade in the module's record for GPA calculations and academic tracking.
 *
 * <p>Usage: {@code grade /mod MODULECODE /grade GRADEVALUE}
 * 
 * <p>Examples of valid grades: A+, A, A-, B+, B, B-, C+, C, D+, D, F, S, U
 */
public class GradeCommand extends Command {

    private final String moduleCode;
    private final String grade;

    /**
     * Constructs a GradeCommand with the specified module code and grade.
     *
     * @param moduleCode the module code (case-insensitive)
     * @param grade      the grade to record (case-insensitive)
     */
    public GradeCommand(String moduleCode, String grade) {
        assert moduleCode != null && !moduleCode.trim().isEmpty() : "Module code must not be null or empty";
        assert grade != null && !grade.trim().isEmpty() : "Grade must not be null or empty";
        this.moduleCode = moduleCode.trim().toUpperCase();
        this.grade = grade.trim().toUpperCase();
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        assert moduleBook != null : "ModuleBook must not be null";
        assert ui != null : "Ui must not be null";
        assert storage != null : "Storage must not be null";

        Module module = moduleBook.getModule(moduleCode);
        if (module == null) {
            throw new ModuleSyncException("Module " + moduleCode + " not found.");
        }

        // Warn if overwriting an existing grade
        if (module.hasGrade()) {
            String oldGrade = module.getGrade();
            ui.showMessage("Warning: Module " + moduleCode + " already has grade " 
                           + oldGrade + ". Overwriting with grade " + grade);
        }

        module.setGrade(grade);
        storage.save(moduleBook);
        ui.showMessage("Grade recorded for module " + moduleCode + ": " + grade);
    }
}
