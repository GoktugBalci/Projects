import java.util.*;
/**
 * Abstract base class representing a person in the system.
 */
public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String department;
    /**
     * Constructs a person with basic information.
     *
     * @param id         Unique identifier of the person
     * @param name       Name of the person
     * @param email      Email address of the person
     * @param department Department the person belongs to
     */
    public Person(String id, String name, String email, String department){
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }
    /** @return Person's ID */
    public String getId() { return id; }
    /** @return Person's name */
    public String getName() { return name; }
    /** @return Person's email */
    public String getEmail() { return email;}
    /** @return Person's department */
    public String getDepartment() { return department;}
}
/**
 * Represents a student in the system. A student can enroll in courses
 * and complete them with a grade.
 */
class Student extends Person {
    private List<Course> enrolledCourses = new ArrayList<>();
    private Map<Course, String> completedCourses = new HashMap<>();
    /**
     * Constructs a Student with the given details.
     *
     * @param id         Student's ID
     * @param name       Student's name
     * @param email      Student's email
     * @param department Department of the student
     */
    public Student(String id, String name, String email, String department){
        super(id, name, email, department);
    }
    /**
     * Enrolls the student in a course.
     *
     * @param course Course to enroll in
     */
    public void enrollCourse(Course course) {
        enrolledCourses.add(course);
    }
    /**
     * Marks a course as completed with the given grade.
     *
     * @param course Course completed
     * @param grade  Grade received
     */
    public void completeCourse(Course course, String grade) {
        completedCourses.put(course, grade);
    }
    /** @return List of courses the student is currently enrolled in */
    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    /** @return Map of completed courses with corresponding grades */
    public Map<Course, String> getCompletedCourses() {
        return completedCourses;
    }
    /**
     * Calculates the student's GPA based on completed courses and grades.
     *
     * @return GPA value (0.0 if no courses completed)
     * @throws InvalidGradeException if any grade is invalid
     */
    public double calculateGPA() throws InvalidGradeException {
        double totalPoints = 0;
        int totalCredits = 0;
        for(Map.Entry<Course, String> entry : completedCourses.entrySet()) {
            Course course = entry.getKey();
            String grade = entry.getValue();
            double gradeValue = course.getGradeValue(grade);
            totalPoints += gradeValue * course.getCredits();
            totalCredits += course.getCredits();
        }
        return totalCredits == 0 ? 0 : totalPoints / totalCredits;
    }
}
/**
 * Represents an academic member (e.g., instructor or professor).
 * They can be assigned to teach courses.
 */
class AcademicMember extends Person{
    private List<Course> coursesTaught = new ArrayList<>();
    /**
     * Constructs an AcademicMember with the given details.
     *
     * @param id         Academic member's ID
     * @param name       Academic member's name
     * @param email      Academic member's email
     * @param department Department of the academic member
     */
    public AcademicMember(String id, String name, String email, String department) {
        super(id, name, email, department);
    }
    /**
     * Assigns a course to be taught by this academic member.
     *
     * @param course Course to assign
     */
    public void assignCourse(Course course) {
        coursesTaught.add(course);
    }
}