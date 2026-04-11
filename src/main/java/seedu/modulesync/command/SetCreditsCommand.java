package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.Module;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Command to set the modular credits for a specific module.
 */
public class SetCreditsCommand extends Command {
    private final String moduleCode;
    private final int credits;

    public SetCreditsCommand(String moduleCode, int credits) {
        assert moduleCode != null && !moduleCode.trim().isEmpty() : "Module code must be provided";
        assert credits >= 0 && credits <= 40 : "Credits must be between 0 and 40";
        this.moduleCode = moduleCode;
        this.credits = credits;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        Module module = moduleBook.getModule(moduleCode);
        if (module == null) {
            throw new ModuleSyncException("Module " + moduleCode.toUpperCase() + " does not exist. "
                    + "Please add a task or grade to it first before setting credits.");
        }
        
        module.setCredits(credits);
        storage.save(moduleBook);
        ui.showCreditsSet(moduleCode, credits);
        
        int totalSemesterCredits = moduleBook.getModules().stream().mapToInt(Module::getCredits).sum();
        if (totalSemesterCredits > 32) {
            ui.showHighSemesterCreditsWarning(totalSemesterCredits);
        }
    }
}
