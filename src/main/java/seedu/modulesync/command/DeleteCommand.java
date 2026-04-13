package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.task.Task;
import seedu.modulesync.ui.Ui;

public class DeleteCommand extends Command {

    private final int displayIndex;

    public DeleteCommand(int displayIndex) {
        this.displayIndex = displayIndex;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        assert moduleBook != null : "ModuleBook must not be null";
        assert storage != null : "Storage must not be null";
        assert ui != null : "Ui must not be null";
        assert displayIndex > 0 : "Display index must be strictly positive";

        int totalBefore = moduleBook.countTotalTasks();
        Task deletedTask = moduleBook.removeTaskByDisplayIndex(displayIndex);
        assert deletedTask != null : "removeTaskByDisplayIndex should return a task when deletion succeeds";

        storage.save(moduleBook);

        int totalAfter = moduleBook.countTotalTasks();
        assert totalAfter == totalBefore - 1 : "Total task count should decrease by exactly 1 after deletion";
        ui.showTaskDeleted(deletedTask, totalAfter);
    }
}
