package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Command to explicitly add a new module to the module book.
 */
public class AddModuleCommand extends Command {
    private final String moduleCode;

    public AddModuleCommand(String moduleCode) {
        assert moduleCode != null && !moduleCode.trim().isEmpty() : "Module code must be provided";
        this.moduleCode = moduleCode;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        moduleBook.getOrCreate(moduleCode);
        storage.save(moduleBook);
        ui.showModuleAdded(moduleCode);
    }
}
