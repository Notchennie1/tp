package seedu.duke.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import seedu.duke.exception.ModuleSyncException;
import seedu.duke.module.Module;
import seedu.duke.module.ModuleBook;
import seedu.duke.task.Task;
import seedu.duke.task.Todo;

public class Storage {
    private final Path filePath;

    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    public ModuleBook load() throws ModuleSyncException {
        ModuleBook moduleBook = new ModuleBook();
        if (!Files.exists(filePath)) {
            ensureParentDirectory();
            return moduleBook;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = decodeTask(line);
                Module module = moduleBook.getOrCreate(task.getModuleCode());
                module.getTasks().add(task);
            }
            return moduleBook;
        } catch (IOException e) {
            throw new ModuleSyncException("Failed to read storage file: " + e.getMessage());
        }
    }

    public void save(ModuleBook moduleBook) throws ModuleSyncException {
        ensureParentDirectory();
        List<String> lines = new ArrayList<>();
        for (Module module : moduleBook.getModules()) {
            for (Task task : module.getTasks().asUnmodifiableList()) {
                lines.add(task.encode());
            }
        }
        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ModuleSyncException("Failed to save tasks: " + e.getMessage());
        }
    }

    private Task decodeTask(String line) throws ModuleSyncException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 4) {
            throw new ModuleSyncException("Corrupted task entry: " + line);
        }
        String moduleCode = parts[0];
        String type = parts[1];
        boolean isDone = parseDone(parts[2]);
        String description = parts[3];

        switch (type) {
        case "T":
            return new Todo(moduleCode, description, isDone);
        case "D":
            if (parts.length < 5) {
                throw new ModuleSyncException("Corrupted deadline entry: " + line);
            }
            try {
                java.time.LocalDateTime byDate;
                if (parts[4].length() <= 10) {
                    java.time.LocalDate datePart = java.time.LocalDate.parse(parts[4]);
                    byDate = datePart.atTime(23, 59);
                } else {
                    java.time.format.DateTimeFormatter formatter = 
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    byDate = java.time.LocalDateTime.parse(parts[4], formatter);
                }
                return new seedu.duke.task.Deadline(moduleCode, description, isDone, byDate);
            } catch (java.time.format.DateTimeParseException e) {
                throw new ModuleSyncException("Corrupted deadline date in entry: " + line);
            }
        default:
            throw new ModuleSyncException("Unsupported task type: " + type);
        }
    }

    private boolean parseDone(String raw) throws ModuleSyncException {
        if ("1".equals(raw)) {
            return true;
        }
        if ("0".equals(raw)) {
            return false;
        }
        throw new ModuleSyncException("Invalid done flag: " + raw);
    }

    private void ensureParentDirectory() throws ModuleSyncException {
        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new ModuleSyncException("Unable to create storage directory: " + e.getMessage());
        }
    }
}





