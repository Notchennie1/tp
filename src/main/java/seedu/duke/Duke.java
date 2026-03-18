package seedu.duke;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Backward-compatible wrapper for the new ModuleSync class. 
 * This allows users to run the program using the old Duke class while still
 * utilising the functionality of the new ModuleSync class.
 */
public class Duke {
    private static final Logger logger = Logger.getLogger(Duke.class.getName());

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        try {
            FileHandler fileHandler = new FileHandler("duke.log", true);
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize file logger", e);
        }

        logger.info("Application successfully starts");
        ModuleSync.main(args);
        logger.info("Application successfully terminates");
    }
}
