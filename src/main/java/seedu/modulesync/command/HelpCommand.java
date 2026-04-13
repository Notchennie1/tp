package seedu.modulesync.command;

import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.ui.Ui;

/**
 * Displays a summary of all available commands to the user.
 */
public class HelpCommand extends Command {

    @Override
    public boolean isMutating() {
        return false;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) {
        ui.showHelp();
    }
}
