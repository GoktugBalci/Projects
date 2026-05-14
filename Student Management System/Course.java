import java.util.*;

/**
 * Represents a university course that can be assigned to students and instructors.
 * It supports grade assignment, average calculation, and grade distribution.
 */
public class Course implements Gradable {
    /** Grade scale mapping letter grades to numerical values. */
    private static final Map<String, Double> gradeScale = new HashMap<>();

    static {
        gradeScale.put("A1", 4.00);
        gradeScale.put("A2", 3.50);
        gradeScale.put("B1", 3.00);
        gradeScale.put("B2", 2.50);
        gradeScale.put("C1", 2.00);
        gradeScale.put("C2", 1.50);
        gradeScale.put("D1", 1.00);
        gradeScale.put("D2", 0.50);
        gradeScale.put("F3", 0.00);
    }

    private String code;
    private String name;
    private String department;
    private int credits;
    private String semester;
    private Program program;
    private AcademicMember instructor;
    private Map<Student, String> grades = new HashMap<>();

    /**
     * Constructs a Course with the given properties.
     *
     * @param code       Unique course code
     * @param name       Course name
     * @param department Department offering the course
     * @param credits    Number of credits
     * @param semester   Semester when the course is offered
     */
    public Course(String code, String name, String department, int credits, String semester) {
        this.code = code;
        this.name = name;
        this.department = department;
        this.credits = credits;
        this.semester = semester;
    }

    /**
     * Assigns an instructor to this course.
     *
     * @param instructor Academic member who will teach the course
     */
    public void assignInstructor(AcademicMember instructor) {
        this.instructor = instructor;
    }

    /**
     * Assigns a grade to a student for this course.
     *
     * @param student Student receiving the grade
     * @param grade   Letter grade to assign
     */
    public void assignGrade(Student student, String grade) {
        grades.put(student, grade);
    }

    /**
     * Sets the program that this course belongs to.
     *
     * @param program The associated academic program
     */
    public void setProgram(Program program) {
        this.program = program;
    }

    /**
     * Enrolls a student in this course. The grade is initialized as null.
     *
     * @param student Student to enroll
     */
    public void enrollStudent(Student student) {
        grades.putIfAbsent(student, null);
    }

    /** @return Course code */
    public String getCode() { return code; }

    /** @return Course name */
    public String getName() { return name; }

    /** @return Department offering the course */
    public String getDepartment() { return department; }

    /** @return Credit value of the course */
    public int getCredits() { return credits; }

    /** @return Semester the course is offered in */
    public String getSemester() { return semester; }

    /** @return Instructor assigned to the course */
    public AcademicMember getInstructor() { return instructor; }

    /** @return Map of students and their assigned grades */
    public Map<Student, String> getGrades() { return grades; }

    /**
     * Converts a letter grade to a numeric value using the grade scale.
     *
     * @param letterGrade Grade to convert (e.g., "A1", "B2")
     * @return Numeric GPA value for the given grade
     * @throws InvalidGradeException if the grade is not recognized
     */
    @Override
    public double getGradeValue(String letterGrade) throws InvalidGradeException {
        if (!gradeScale.containsKey(letterGrade)) {
            throw new InvalidGradeException("The grade " + letterGrade + " is not valid");
        }
        return gradeScale.get(letterGrade);
    }

    /**
     * Calculates the average grade for this course based on all assigned grades.
     *
     * @return Average numeric grade (0 if no grades assigned)
     * @throws InvalidGradeException if any grade is invalid
     */
    public double getAverageGrade() throws InvalidGradeException {
        double sum = 0;
        int count = 0;
        for (String grade : grades.values()) {
            if (grade != null) {
                sum += getGradeValue(grade);
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    /**
     * Calculates the distribution of grades for this course.
     *
     * @return Map of letter grades to the number of students who received each
     */
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        for (String grade : grades.values()) {
            if (grade != null) {
                distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
            }
        }
        return distribution;
    }
}

