import java.util.*;

/**
 * Represents an academic program (e.g., Bachelor's, Master's) offered by a department.
 * A program includes a set of courses, is associated with a department, and has
 * attributes such as degree level and total credit requirement.
 */
public class Program {
    /** Unique code identifying the program */
    private String code;

    /** Name of the academic program */
    private String name;

    /** Description of the program’s scope or content */
    private String description;

    /** Department offering this program */
    private Department department;

    /** Degree level (e.g., Bachelor's, Master's, PhD) */
    private String degreeLevel;

    /** Total credits required to complete the program */
    private int totalCredits;

    /** List of courses included in the program */
    private List<Course> courses = new ArrayList<>();

    /**
     * Constructs a Program with specified attributes.
     *
     * @param code         Unique program code
     * @param name         Name of the program
     * @param description  Description of the program
     * @param department   Department offering the program
     * @param degreeLevel  Level of the degree (e.g., Bachelor's, Master's)
     * @param totalCredits Total credits required for completion
     */
    public Program(String code, String name, String description, Department department,
                   String degreeLevel, int totalCredits) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.department = department;
        this.degreeLevel = degreeLevel;
        this.totalCredits = totalCredits;
    }

    /**
     * Adds a course to the program’s course list.
     *
     * @param course Course to add to the program
     */
    public void addCourse(Course course) {
        courses.add(course);
    }

    /**
     * @return List of courses in the program
     */
    public List<Course> getCourses() { return courses; }

    /**
     * @return Unique code of the program
     */
    public String getCode() { return code; }

    /**
     * @return Name of the program
     */
    public String getName() { return name; }

    /**
     * @return Department offering the program
     */
    public Department getDepartment() { return department; }

    /**
     * @return Degree level of the program (e.g., Bachelor's)
     */
    public String getDegreeLevel() { return degreeLevel; }

    /**
     * @return Total credit requirement for the program
     */
    public int getTotalCredits() { return totalCredits; }
}
