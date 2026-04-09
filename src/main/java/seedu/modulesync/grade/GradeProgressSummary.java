package seedu.modulesync.grade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the rendered grade-progress summary across tracked semesters.
 */
public class GradeProgressSummary {

    private final List<SemesterSummary> semesterSummaries;

    /**
     * Constructs a grade-progress summary from the given semester summaries.
     *
     * @param semesterSummaries the ordered semester summaries to expose
     */
    public GradeProgressSummary(List<SemesterSummary> semesterSummaries) {
        assert semesterSummaries != null : "Semester summaries must not be null";
        this.semesterSummaries = new ArrayList<>(semesterSummaries);
    }

    /**
     * Returns the ordered semester summaries contained in this result.
     *
     * @return the semester summaries
     */
    public List<SemesterSummary> getSemesterSummaries() {
        return Collections.unmodifiableList(semesterSummaries);
    }

    /**
     * Returns whether this summary contains at least one semester with recorded grades.
     *
     * @return {@code true} if the summary has semester entries
     */
    public boolean hasSemesterSummaries() {
        return !semesterSummaries.isEmpty();
    }

    /**
     * Represents the grade summary for one semester together with its cumulative progress.
     */
    public static final class SemesterSummary {

        private final String semesterName;
        private final boolean currentSemester;
        private final boolean archived;
        private final List<ModuleResult> moduleResults;
        private final double semesterGradePoints;
        private final int semesterCredits;
        private final double cumulativeGradePoints;
        private final int cumulativeCredits;
        private final int cumulativeSemesterCount;

        /**
         * Constructs a semester summary with semester and cumulative CAP data.
         *
         * @param semesterName the semester label
         * @param currentSemester whether this semester is currently selected
         * @param archived whether this semester is archived
         * @param moduleResults the graded module results in this semester
         * @param semesterGradePoints the semester's total CAP-bearing grade points
         * @param semesterCredits the semester's total CAP-bearing credits
         * @param cumulativeGradePoints the cumulative CAP-bearing grade points up to this semester
         * @param cumulativeCredits the cumulative CAP-bearing credits up to this semester
         * @param cumulativeSemesterCount the number of graded semesters up to this semester
         */
        public SemesterSummary(String semesterName, boolean currentSemester, boolean archived,
                               List<ModuleResult> moduleResults, double semesterGradePoints, int semesterCredits,
                               double cumulativeGradePoints, int cumulativeCredits,
                               int cumulativeSemesterCount) {
            assert semesterName != null && !semesterName.isBlank() : "Semester name must not be blank";
            assert moduleResults != null : "Module results must not be null";
            this.semesterName = semesterName;
            this.currentSemester = currentSemester;
            this.archived = archived;
            this.moduleResults = new ArrayList<>(moduleResults);
            this.semesterGradePoints = semesterGradePoints;
            this.semesterCredits = semesterCredits;
            this.cumulativeGradePoints = cumulativeGradePoints;
            this.cumulativeCredits = cumulativeCredits;
            this.cumulativeSemesterCount = cumulativeSemesterCount;
        }

        /**
         * Returns the semester label.
         *
         * @return the semester label
         */
        public String getSemesterName() {
            return semesterName;
        }

        /**
         * Returns whether this semester is currently selected in the CLI.
         *
         * @return {@code true} if this is the current semester
         */
        public boolean isCurrentSemester() {
            return currentSemester;
        }

        /**
         * Returns whether this semester is archived.
         *
         * @return {@code true} if this semester is archived
         */
        public boolean isArchived() {
            return archived;
        }

        /**
         * Returns the graded module results in this semester.
         *
         * @return the graded module results
         */
        public List<ModuleResult> getModuleResults() {
            return Collections.unmodifiableList(moduleResults);
        }

        /**
         * Returns whether this semester has at least one CAP-bearing module.
         *
         * @return {@code true} if the semester CAP can be calculated
         */
        public boolean hasSemesterCap() {
            return semesterCredits > 0;
        }

        /**
         * Returns the semester's calculated CAP.
         *
         * @return the semester CAP
         */
        public double getSemesterCap() {
            assert hasSemesterCap() : "Semester CAP is only available when semester credits are positive";
            return semesterGradePoints / semesterCredits;
        }

        /**
         * Returns the semester's CAP-bearing credits.
         *
         * @return the semester CAP-bearing credits
         */
        public int getSemesterCredits() {
            return semesterCredits;
        }

        /**
         * Returns whether the cumulative CAP can be calculated up to this semester.
         *
         * @return {@code true} if cumulative CAP-bearing credits exist
         */
        public boolean hasCumulativeCap() {
            return cumulativeCredits > 0;
        }

        /**
         * Returns the cumulative CAP up to and including this semester.
         *
         * @return the cumulative CAP
         */
        public double getCumulativeCap() {
            assert hasCumulativeCap() : "Cumulative CAP is only available when cumulative credits are positive";
            return cumulativeGradePoints / cumulativeCredits;
        }

        /**
         * Returns the cumulative CAP-bearing credits up to this semester.
         *
         * @return the cumulative CAP-bearing credits
         */
        public int getCumulativeCredits() {
            return cumulativeCredits;
        }

        /**
         * Returns the number of graded semesters counted up to this semester.
         *
         * @return the cumulative graded semester count
         */
        public int getCumulativeSemesterCount() {
            return cumulativeSemesterCount;
        }
    }

    /**
     * Represents one graded module entry within a semester summary.
     */
    public static final class ModuleResult {

        private final String moduleCode;
        private final int credits;
        private final String grade;
        private final Double gradePoint;

        /**
         * Constructs a graded module result.
         *
         * @param moduleCode the module code
         * @param credits the module credits
         * @param grade the recorded letter grade
         * @param gradePoint the grade-point value, or {@code null} if not CAP-bearing
         */
        public ModuleResult(String moduleCode, int credits, String grade, Double gradePoint) {
            assert moduleCode != null && !moduleCode.isBlank() : "Module code must not be blank";
            assert credits >= 0 : "Module credits must not be negative";
            assert grade != null && !grade.isBlank() : "Grade must not be blank";
            this.moduleCode = moduleCode;
            this.credits = credits;
            this.grade = grade;
            this.gradePoint = gradePoint;
        }

        /**
         * Returns the module code.
         *
         * @return the module code
         */
        public String getModuleCode() {
            return moduleCode;
        }

        /**
         * Returns the module credits.
         *
         * @return the module credits
         */
        public int getCredits() {
            return credits;
        }

        /**
         * Returns the recorded letter grade.
         *
         * @return the recorded letter grade
         */
        public String getGrade() {
            return grade;
        }

        /**
         * Returns whether this result has a CAP-bearing grade-point value.
         *
         * @return {@code true} if a grade-point value is available
         */
        public boolean hasGradePoint() {
            return gradePoint != null;
        }

        /**
         * Returns the CAP-bearing grade-point value for this module.
         *
         * @return the grade-point value
         */
        public double getGradePoint() {
            assert hasGradePoint() : "Grade point is only available for CAP-bearing grades";
            return gradePoint;
        }
    }
}
