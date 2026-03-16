package seedu.duke;

import java.nio.file.Paths;
import java.util.Scanner;

import seedu.duke.command.Command;
import seedu.duke.exception.ModuleSyncException;
import seedu.duke.module.ModuleBook;
import seedu.duke.parser.Parser;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

public class ModuleSync {
    private final Storage storage;
    private final ModuleBook moduleBook;
    private final Parser parser;
    private final Ui ui;

    public ModuleSync() throws ModuleSyncException {
        this.storage = new Storage(Paths.get("data", "modules.txt"));
        this.moduleBook = storage.load();
        this.parser = new Parser();
        this.ui = new Ui(new Scanner(System.in));
    }

    public void run() {
        ui.showWelcome();
        boolean exit = false;
        while (!exit) {
            String fullCommand = ui.readCommand();
            try {
                Command command = parser.parse(fullCommand);
                command.execute(moduleBook, storage, ui);
                exit = command.isExit();
            } catch (ModuleSyncException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Main entry-point for the ModuleSync application.
     */
    public static void main(String[] args) {
        try {
            new ModuleSync().run();
        } catch (ModuleSyncException e) {
            System.out.println("Failed to start: " + e.getMessage());
        }
    }
}
