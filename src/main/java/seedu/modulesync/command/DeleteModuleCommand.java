package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Command to explicitly delete a module from the module book.
 */
public class DeleteModuleCommand extends Command {
    private final String moduleCode;

    public DeleteModuleCommand(String moduleCode) {
        assert moduleCode != null && !moduleCode.trim().isEmpty() : "Module code must be provided";
        this.moduleCode = moduleCode;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        seedu.modulesync.module.Module removedModule = moduleBook.deleteModule(moduleCode);
        if (removedModule == null) {
            throw new ModuleSyncException("Module " + moduleCode.toUpperCase() + " does not exist.");
        }
        storage.save(moduleBook);
        ui.showModuleDeleted(moduleCode);
    }
}
