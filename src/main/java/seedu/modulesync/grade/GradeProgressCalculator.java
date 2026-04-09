package seedu.modulesync.grade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import seedu.modulesync.module.Module;
import seedu.modulesync.semester.Semester;
import seedu.modulesync.semester.SemesterBook;

/**
 * Calculates the semester-by-semester grade progress summary for the CLI.
 */
public class GradeProgressCalculator {

    private static final String GRADE_A_PLUS = "A+";
    private static final String GRADE_A = "A";
    private static final String GRADE_A_MINUS = "A-";
    private static final String GRADE_B_PLUS = "B+";
    private static final String GRADE_B = "B";
    private static final String GRADE_B_MINUS = "B-";
    private static final String GRADE_C_PLUS = "C+";
    private static final String GRADE_C = "C";
    private static final String GRADE_D_PLUS = "D+";
    private static final String GRADE_D = "D";
    private static final String GRADE_F = "F";
    private static final String GRADE_CS = "CS";
    private static final String GRADE_CU = "CU";
    private static final String GRADE_S = "S";
    private static final String GRADE_U = "U";

    private static final Map<String, Double> CAP_GRADE_POINTS = createCapGradePoints();
    private static final Set<String> NON_CAP_GRADES = createNonCapGrades();

    /**
     * Builds a grade-progress summary covering all semesters with recorded grades.
     *
     * @param semesterBook the semester book to summarise
     * @return the calculated summary
     */
    public GradeProgressSummary calculateSummary(SemesterBook semesterBook) {
        assert semesterBook != null : "SemesterBook must not be null when calculating grade progress";

        List<GradeProgressSummary.SemesterSummary> semesterSummaries = new ArrayList<>();
        double cumulativeGradePoints = 0;
        int cumulativeCredits = 0;
        int cumulativeSemesterCount = 0;
        String currentSemesterName = semesterBook.getCurrentSemesterName();

        for (Semester semester : semesterBook.getAllSemesters()) {
            List<GradeProgressSummary.ModuleResult> moduleResults = collectModuleResults(semester);
            if (moduleResults.isEmpty()) {
                continue;
            }

            double semesterGradePoints = calculateSemesterGradePoints(moduleResults);
            int semesterCredits = calculateSemesterCredits(moduleResults);
            cumulativeGradePoints += semesterGradePoints;
            cumulativeCredits += semesterCredits;
            cumulativeSemesterCount++;

            boolean isCurrentSemester = semester.getName().equals(currentSemesterName);
            GradeProgressSummary.SemesterSummary semesterSummary = new GradeProgressSummary.SemesterSummary(
                    semester.getName(),
                    isCurrentSemester,
                    semester.isArchived(),
                    moduleResults,
                    semesterGradePoints,
                    semesterCredits,
                    cumulativeGradePoints,
                    cumulativeCredits,
                    cumulativeSemesterCount);
            semesterSummaries.add(semesterSummary);
        }

        return new GradeProgressSummary(semesterSummaries);
    }

    /**
     * Collects all recorded module grades from one semester.
     *
     * @param semester the semester to inspect
     * @return the graded module results in module insertion order
     */
    private List<GradeProgressSummary.ModuleResult> collectModuleResults(Semester semester) {
        List<GradeProgressSummary.ModuleResult> moduleResults = new ArrayList<>();
        for (Module module : semester.getModuleBook().getModules()) {
            if (!module.hasGrade()) {
                continue;
            }

            Double gradePoint = lookupGradePoint(module.getGrade());
            GradeProgressSummary.ModuleResult moduleResult = new GradeProgressSummary.ModuleResult(
                    module.getCode(),
                    module.getCredits(),
                    module.getGrade(),
                    gradePoint);
            moduleResults.add(moduleResult);
        }
        return moduleResults;
    }

    /**
     * Calculates the semester's total CAP-bearing grade points.
     *
     * @param moduleResults the graded modules in the semester
     * @return the semester's total CAP-bearing grade points
     */
    private double calculateSemesterGradePoints(List<GradeProgressSummary.ModuleResult> moduleResults) {
        double semesterGradePoints = 0;
        for (GradeProgressSummary.ModuleResult moduleResult : moduleResults) {
            if (!moduleResult.hasGradePoint()) {
                continue;
            }
            semesterGradePoints += moduleResult.getGradePoint() * moduleResult.getCredits();
        }
        return semesterGradePoints;
    }

    /**
     * Calculates the semester's total CAP-bearing credits.
     *
     * @param moduleResults the graded modules in the semester
     * @return the semester's total CAP-bearing credits
     */
    private int calculateSemesterCredits(List<GradeProgressSummary.ModuleResult> moduleResults) {
        int semesterCredits = 0;
        for (GradeProgressSummary.ModuleResult moduleResult : moduleResults) {
            if (!moduleResult.hasGradePoint()) {
                continue;
            }
            semesterCredits += moduleResult.getCredits();
        }
        return semesterCredits;
    }

    /**
     * Looks up the CAP-bearing grade-point value for the given letter grade.
     *
     * @param grade the recorded letter grade
     * @return the grade-point value, or {@code null} if the grade does not affect CAP
     */
    private Double lookupGradePoint(String grade) {
        assert grade != null && !grade.isBlank() : "Grade must not be blank when looking up grade points";

        String normalizedGrade = grade.toUpperCase();
        if (NON_CAP_GRADES.contains(normalizedGrade)) {
            return null;
        }
        return CAP_GRADE_POINTS.get(normalizedGrade);
    }

    /**
     * Creates the CAP-bearing grade-point lookup table.
     *
     * @return the CAP-bearing grade-point lookup table
     */
    private static Map<String, Double> createCapGradePoints() {
        Map<String, Double> gradePoints = new HashMap<>();
        gradePoints.put(GRADE_A_PLUS, 5.0);
        gradePoints.put(GRADE_A, 5.0);
        gradePoints.put(GRADE_A_MINUS, 4.5);
        gradePoints.put(GRADE_B_PLUS, 4.0);
        gradePoints.put(GRADE_B, 3.5);
        gradePoints.put(GRADE_B_MINUS, 3.0);
        gradePoints.put(GRADE_C_PLUS, 2.5);
        gradePoints.put(GRADE_C, 2.0);
        gradePoints.put(GRADE_D_PLUS, 1.5);
        gradePoints.put(GRADE_D, 1.0);
        gradePoints.put(GRADE_F, 0.0);
        return gradePoints;
    }

    /**
     * Creates the set of grades that do not contribute to CAP.
     *
     * @return the set of grades excluded from CAP
     */
    private static Set<String> createNonCapGrades() {
        Set<String> nonCapGrades = new HashSet<>();
        nonCapGrades.add(GRADE_CS);
        nonCapGrades.add(GRADE_CU);
        nonCapGrades.add(GRADE_S);
        nonCapGrades.add(GRADE_U);
        return nonCapGrades;
    }
}
