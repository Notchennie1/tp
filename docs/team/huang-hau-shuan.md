# Huang Hau Shuan - Project Portfolio Page

## Project: ModuleSync

ModuleSync is a desktop CLI-based task manager designed to help university students achieve a
balanced academic life through structured organisation of their module-related tasks. The user
interacts with it using a Command Line Interface (CLI), and all data is stored locally in
human-editable text files. It is written in Java 17, and has about 6 kLoC.

Given below are my contributions to the project.

---

## New Features

### Feature 1: Core OOP Architecture (Parser, Storage, Task, Ui, Command, Exception, Module)
**What it does:** Established the foundational class structure of the entire application using
Object-Oriented Programming principles. This includes the abstract `Task` model, the `Command`
abstraction, the `Parser`, `Storage`, `Ui`, `ModuleBook`, `Module`, `TaskList`, and
`ModuleSyncException` classes.

**Justification:** Without a clean, well-designed base architecture, the team would have no
consistent framework to build features on. By defining clear responsibilities for each class
(`Parser` only parses, `Command` only executes, `Storage` only persists), the codebase remains
maintainable and extensible as the project grows.

**Highlights:** The `Command` abstraction allows every feature to be implemented as a self-contained
class with a single `execute(ModuleBook, Storage, Ui)` method, making it trivial for teammates to
add new commands without touching unrelated code. The `Task` abstract class uses a nullable
`Integer` weightage field so that weighted and unweighted tasks coexist naturally within the same
model without a separate boolean flag.

---

### Feature 2: Semester OOP Structure, Persistence, and Read-Only Enforcement
**What it does:** Designed and implemented the multi-semester data model:
`SemesterBook` → `Semester` → `ModuleBook` → `Module` → `TaskList` → `Task`. Each semester is
persisted to its own `.txt` file, and a `current.txt` pointer tracks the active semester.
Archived semesters are enforced as read-only — all mutating commands are blocked at the
`ModuleSync` main loop level via `Command#isMutating()`.

**Justification:** A student tracks multiple semesters across their degree. Isolating each semester
into its own data file makes historical data safe and the storage format human-readable and
debuggable. The read-only guard at the dispatch level (rather than inside each command) means that
no individual command author needs to handle archived-semester logic — it is enforced centrally
once.

**Highlights:**
- Introduced `SemesterCommand` as an abstract subclass of `Command` that bypasses the read-only
  guard — this is the correct design because semester-lifecycle operations (e.g. archiving a
  semester) must be executable even when the current semester is already read-only.
- The `SemesterBook` uses a `LinkedHashMap` to preserve insertion order for display purposes while
  enabling O(1) lookup by name.
- `SemesterStorage` uses a dedicated `save(SemesterBook)` method that rewrites each semester file
  atomically; `current.txt` is always the last file written to minimise the risk of an inconsistent
  state on crash.

---

### Feature 3: Module Grade and Credits Persistence (`grade`, `setcredits`)
**What it does:** Extended the `Module` model with `grade` (String, nullable) and `credits` (int)
fields. Added `#MOD` metadata lines to the per-module storage format so that grade and credit
information is persisted alongside tasks. Implemented `hasGrade()` as a null-safe guard to prevent
defensive null checks throughout the codebase.

**Justification:** CAP calculation and semester statistics are meaningless without grade and credit
data. Storing this metadata in the same file as the module's tasks keeps each semester's data
self-contained and portable.

**Highlights:** The `#MOD` line format (`#MOD|CODE|GRADE|CREDITS|ARCHIVED`) was designed to be
forward-compatible: missing fields on older files are treated as absent without crashing the loader.

---

### Feature 4: Per-Module Statistics (`stats /mod MODULE_CODE`)
**What it does:** Computes and displays a task-completion breakdown for a single module: total
tasks, completed on-time count and percentage, late count and percentage, currently active count
and percentage, and average days before/after deadline at completion time.

**Justification:** Raw task counts are insufficient for a student who wants to understand their own
working habits. The on-time/late distinction, together with average completion time relative to the
deadline, surfaces actionable insight about whether the user is keeping up with their workload.

**Highlights:**
- The private static inner class `ModuleStats` is a value object that holds all computed figures.
  `computeStats(List<Task>)` returns a `ModuleStats` instance, keeping `execute()` as a
  high-level narrative (SLAP).
- Uses `java.util.logging.Logger` with `LOGGER.fine()` — no output goes to the terminal; all
  diagnostic records go to `modulesync.log`.
- Correctly handles the legacy case where `completedAt` is `null` (tasks marked done before
  timestamp tracking was introduced) by treating them as on-time.

---

### Feature 5: Inline and Post-Creation Task Weightage (`add /w PERCENT`, `setweight`)
**What it does:** Allows users to attach a percentage grade-weight to any task, either at creation
time via the optional `/w PERCENT` flag or afterwards via `setweight TASK_NUMBER PERCENT`. The
weightage is validated to a 0–100 integer range, displayed inline in all list views (`[25%]`), and
factored into priority score calculation.

**Justification:** Not all tasks carry equal academic weight. Annotating tasks with their
contribution to the module grade lets users prioritise high-impact work at a glance.

**Highlights:** Weightage is validated once in `Parser#parseWeightage` using named constants
(`MIN_WEIGHTAGE`, `MAX_WEIGHTAGE`), keeping every command constructor free of user-input validation
logic. The error message explicitly states that the `%` symbol must not be included, which was a
documented usability issue that was fixed.

---

### Feature 6: Priority Score and `list /top` Algorithm Improvement
**What it does:** Improved the task priority algorithm so that deadline proximity is the **primary
driver** of priority, with weightage as a secondary contributor. A task due imminently will always
outrank a higher-weighted task due much later.

**Justification:** The original algorithm ranked purely by weightage, meaning a 10%-weighted
assignment due today ranked below a 50%-weighted final due in a week — the opposite of what a
student needs. The revised algorithm reflects how students actually prioritise work.

**Highlights:**
- Preserved the original priority structure authored by a teammate (weightage as base score);
  modified only the urgency contribution so that deadline proximity scales from ~181 points
  (overdue/due today) down to ~1 point (≥30 days), making urgency dominate at short horizons.
- Updated both the DG and UG to document the formula, score ranges, and an illustrative example
  so users can interpret `[Priority: N]` labels.

---

### Feature 7: `help` Command
**What it does:** Displays a complete, categorised reference of all available commands directly in
the terminal, removing the need to open the User Guide for basic command lookup.

**Justification:** CLI applications that require external documentation for basic operation create
unnecessary friction. The `help` command makes the application self-documenting for new users.

**Highlights:** The command list is grouped into seven categories (Task Management, Listing,
Modules, Grades, Stats, Checks, Semester Management, General) and every entry shows the exact
command format with correct syntax — validated against the `Parser` implementation to ensure
accuracy.

---

## Code Contributed
[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=Huang-Hau-Shuan&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other)

---

## Enhancements to Existing Features

- **Logger suppression** — implemented `ModuleSync#configureLogging()` using a private static
  helper that removes the JUL root `ConsoleHandler` and attaches a `FileHandler` writing to
  `modulesync.log`. This ensures no log records ever appear in the user's terminal, while full
  diagnostic output remains available in the log file.
- **`duke` → `modulesync` refactor** — renamed all package identifiers, file references, and log
  file names from `duke` to `modulesync` to align with the product name throughout the codebase.
- **`GradePointScale` refactor** — replaced a long `if-else` chain mapping letter grades to grade
  points with a `LinkedHashMap` lookup table, eliminating duplicate logic and making it trivial
  to add or change grade mappings in one place.
- **SLAP refactors** — extracted `computeModuleStatLine`, `ModuleStatLine`, and `ModuleStats` inner
  classes to eliminate three-level nesting in statistics computation; extracted `parseWeightage`,
  `buildAddDeadlineCommand`, `extractFlagValue`, `findFlagPosition`, and
  `validateAllFlagsRecognized` helpers in `Parser` to ensure each method reads as a high-level
  narrative.
- **Magic number/string elimination** — extracted all command keyword constants, prefix strings,
  length constants, and validation bounds into named `private static final` constants in `Parser`
  and `Task`.
- **Defensive assertions** — added `assert` statements to constructors and `execute()` methods
  across `Task`, `TaskList`, `Module`, `ModuleBook`, `AddTodoCommand`, `SetWeightCommand`,
  `StatsCommand`, `ListDeadlinesCommand`, and `HelpCommand` to document and enforce internal
  invariants. Assertions document preconditions for future developers, not user input errors.
- **`parseWeightage` usability fix** — improved the error message to explicitly state that the `%`
  symbol must not be included (e.g. use `25`, not `25%`), matching the UG guidance.

---

## Testing

- **`StatsCommandTest`** (7 tests) — covers all branches of `computeStats`: all-not-done, todos
  marked done (counted as on-time), deadline done before due date, deadline done after due date,
  todo-only module (NaN avg), empty module, nonexistent module. This is the most exhaustively
  branched test class in the project.
- **`SetWeightCommandTest`** (6 tests) — BVA for boundary weightages (0 and 100 accepted), fresh
  set, overwrite, previous-null detection, invalid index.
- **`SemesterBookTest`** (11 tests) — covers `switchOrCreate` (new, existing, two-semester switch,
  blank name throws), `getCurrentSemester` (no semester set throws), `isCurrentSemesterReadOnly`
  (active returns false, archived returns true), archive/unarchive, `hasSemesters`, `getAllSemesters`.
- **`MarkCommandTest`**, **`UnmarkCommandTest`**, **`AddTodoCommandTest`**, **`DeleteCommandTest`**
  (partial), **`TaskListTest`**, **`TodoTest`** — unit tests for model-layer and command-layer
  correctness.
- **`HelpCommandTest`** (4 tests) — verifies `isMutating()` returns false, no exception on
  execution, output contains all key command categories, and the `%` symbol note is present.

---

## Documentation

### User Guide
- Wrote the `add` command documentation (both todo and deadline variants with optional `/w`)
- Wrote the `setweight` and `editweight` documentation including the `%` symbol clarification
- Wrote the `stats /mod` documentation including example output
- Wrote the `list /top` priority score section including the formula table, score ranges, and an
  illustrative worked example (`[Priority: 191]` vs `[Priority: 189]`)
- Updated the Glossary entry for *Priority score* with the full formula description

### Developer Guide
- **Architecture chapter** — wrote the overarching architectural description and created the
  `ArchitectureDiagram.puml` UML component diagram (replacing the original text-based diagram)
- **Data Model class diagram** — created `DataModelClassDiagram.puml` showing the full
  `SemesterBook` → `Task` hierarchy with correct composition arrows, abstract `Task` class, and
  navigability
- **Command Architecture class diagram** — created `CommandArchitectureClassDiagram.puml` showing
  `Command` and `SemesterCommand` as abstract classes with all concrete subclasses and correct
  dependency arrows
- **Implementation chapter** — rewrote the entire chapter as a single-voice document: removed PPP
  markers, merged redundant feature sections, removed four identical per-feature class diagrams,
  added the new **Managing the Semester Lifecycle** section with `SemesterLifecycleSequenceDiagram.puml`
- **Weightage diagrams** — created `AddWeightageSequenceDiagram.puml`, `WeightedTaskObjectDiagram.puml`,
  and `WeightageValidationActivityDiagram.puml`
- **UML corrections** — corrected `skinparam sequenceParticipant underline` violations across all
  sequence diagrams, fixed `new` keyword on constructor arrows (replaced with `create` pattern),
  fixed return arrow destinations (`ui --> cmd` not `ui --> user`), added activation bars on
  `Module`, `TaskList`, and `Task` in loop interactions, fixed `ArchitectureDiagram` arrow
  directions

---

## Project Management

- **Architecture Lead:** Designed and implemented the core OOP structure and semester data model
  that the entire team builds on
- **Coding Standards Enforcement:** Enforced naming conventions, Javadoc comments, no magic numbers,
  SLAP refactoring, and correct logging usage across the codebase
- **Issue Fixes:** Resolved logger console output leak, `duke`→`modulesync` naming, priority
  algorithm inaccuracy, `%` symbol parse error, and `day(s)` pluralisation across multiple PRs
