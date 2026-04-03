# User Guide

## Introduction

ModuleSync is a CLI app for tracking module-related tasks (e.g., assignments, quizzes, readings) grouped by module code.
It helps you capture tasks quickly, view only what matters (e.g., unfinished tasks for a module), and keep your list accurate.

## Quick Start

1. Ensure that you have Java 17 or above installed.
1. Download the latest version of `ModuleSync` from [here](http://link.to/modulesync).
1. Run the app.
	- If you have a JAR file: `java -jar modulesync.jar`
	- If you are running from source: `./gradlew run`
1. Type a command and press Enter.

## Features 

Notes about command formats:

* Words in `UPPER_CASE` are parameters to be supplied by you.
* Words in square brackets `[OPTIONAL]` are optional parameters.
* Module codes are case-insensitive (e.g., `cs2113` is treated as `CS2113`).
* Task indices are **1-based** and refer to the numbering shown by `list`.

### Adding a task: `add`
Adds a task under a module. If `/due` is provided, the task is recorded as a deadline.

Format: `add /mod MODULE_CODE /task DESCRIPTION [/due YYYY-MM-DD[-HHmm]] [/w PERCENT]`

* `/due` is optional.
	* `YYYY-MM-DD` is interpreted as `23:59` on that date.
	* `YYYY-MM-DD-HHmm` (e.g., `2026-04-01-2359`) specifies an exact time.
* `/w` is optional and must be an integer from `0` to `100` (representing percentage weightage).
* Both parameters can be added in any order.

Examples:

* `add /mod CS2113 /task Quiz 2`
* `add /mod CS2113 /task Final Project /w 25`
* `add /mod CS2113 /task Submit iP /due 2026-04-01`
* `add /mod CS2113 /task Submit iP /due 2026-04-01-2359 /w 10`

### Listing tasks: `list`
Shows tasks in the list.

Formats:

* `list` — lists all tasks across all modules.
* `list /mod MODULE_CODE` — lists tasks for a specific module.
* `list /deadlines` — lists only deadline tasks (sorted chronologically).

Examples:

* `list`
* `list /mod CS2113`
* `list /deadlines`

### Listing not-done tasks for a module: `list /notdone`
Shows only tasks that are not marked done for a specific module.

Format: `list /notdone /mod MODULE_CODE`

* The output uses the **same global indices as `list`**, so you can use those indices for `mark`, `unmark`, or `delete`.
* `list /mod MODULE_CODE /notdone` is also accepted.

Examples:

* `list /notdone /mod CS2113`
* `list /mod CS2113 /notdone`

### Marking a task as done: `mark`
Marks a task as completed.

Format: `mark TASK_NUMBER`

* Task number refers to the index shown in the `list` output (1-based).

Examples:

* `mark 1`
* `mark 5`

### Unmarking a task as not done: `unmark`
Marks a task as incomplete (undoes a previous `mark`).

Format: `unmark TASK_NUMBER`

* Task number refers to the index shown in the `list` output (1-based).

Examples:

* `unmark 1`
* `unmark 3`

### Deleting a task: `delete`
Deletes a task using its **display index** from `list`.

Format: `delete TASK_NUMBER`

* Task number refers to the index shown in the `list` output (1-based).

Example:

* `delete 3`

### Exiting the application: `exit`
Closes the application and saves all data.

Format: `exit`

Example:

* `exit`

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: Your task data is stored in a file called `modules.txt` in the `data/` folder. You can copy this file to the `data/` folder of ModuleSync on another computer to transfer all your tasks.

**Q**: Can I edit the `modules.txt` file directly?

**A**: Yes, you can manually edit the `modules.txt` file as it is a plain text file. However, be careful with the format to avoid corruption. Make sure to follow the same format as the existing entries.

**Q**: What happens when I mark a task as done?

**A**: Marking a task toggles its status. If it's not done, it will be marked as done (and will not appear in `list /notdone`). You can unmark it later using the `unmark` command.

**Q**: Will my data be lost if the application crashes?

**A**: As long as your last command was successfully executed, all data is saved to `modules.txt` immediately after each command. If a command fails, no data is saved for that command.

## Command Summary

| Action | Format |
|--------|--------|
| Add task | `add /mod MODULE_CODE /task DESCRIPTION [/due YYYY-MM-DD[-HHmm]] [/w PERCENT]` |
| List all tasks | `list` |
| List tasks by module | `list /mod MODULE_CODE` |
| List upcoming deadlines | `list /deadlines` |
| List not-done tasks by module | `list /notdone /mod MODULE_CODE` |
| Mark task as done | `mark TASK_NUMBER` |
| Unmark task as not done | `unmark TASK_NUMBER` |
| Delete task | `delete TASK_NUMBER` |
| Exit application | `exit` |
