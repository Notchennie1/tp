package seedu.modulesync.command;

import seedu.modulesync.exception.ModuleSyncException;
import seedu.modulesync.module.Module;
import seedu.modulesync.module.ModuleBook;
import seedu.modulesync.storage.Storage;
import seedu.modulesync.task.Task;
import seedu.modulesync.ui.Ui;

public class MarkCommand extends Command {
    private final int taskNumber;
    private final String moduleCode;
    private final boolean isMarkAll;

    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
        this.moduleCode = null;
        this.isMarkAll = false;
    }

    public MarkCommand(String moduleCode) {
        this.taskNumber = -1;
        this.moduleCode = moduleCode;
        this.isMarkAll = true;
    }

    @Override
    public void execute(ModuleBook moduleBook, Storage storage, Ui ui) throws ModuleSyncException {
        if (isMarkAll) {
            Module module = moduleBook.getModule(moduleCode);
            if (module == null) {
                throw new ModuleSyncException("Module does not exist: " + moduleCode.toUpperCase());
            }
            int taskCount = module.getTasks().size();
            if (taskCount == 0) {
                ui.showNoTasksToMark(moduleCode);
                return;
            }
            for (Task task : module.getTasks().asUnmodifiableList()) {
                task.markDone();
            }
            storage.save(moduleBook);
            ui.showAllTasksMarked(moduleCode, taskCount);
        } else {
            Task task = moduleBook.getTaskByDisplayIndex(taskNumber);
            task.markDone();
            storage.save(moduleBook);
            ui.showTaskMarked(task, taskNumber);
        }
    }
}
