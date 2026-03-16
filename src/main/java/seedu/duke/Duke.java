package seedu.duke;

/**
 * Backward-compatible wrapper for the new ModuleSync class. 
 * This allows users to run the program using the old Duke class while still
 * utilising the functionality of the new ModuleSync class.
 */
public class Duke {
    public static void main(String[] args) {
        ModuleSync.main(args);
    }
}
