/**
 * Represents an entity that can provide a numeric value for a given letter grade.
 */
public interface Gradable {
    /**
     * Converts a letter grade to its corresponding numeric grade value.
     *
     * @param letterGrade the letter grade to convert (e.g., "A1", "B2", etc.)
     * @return the numeric value of the letter grade
     * @throws InvalidGradeException if the letter grade is not valid
     */
    double getGradeValue(String letterGrade) throws InvalidGradeException;
}

