import java.io.PrintWriter;
import java.util.*;

/**
 * Manages academic entities like students, faculty, departments, programs, and courses.
 * Provides methods to add data, assign courses and grades, and generate detailed reports.
 */
public class StudentManagementSystem {
    /** Map of students by their ID */
    private Map<String, Student> students = new HashMap<>();

    /** Map of academic members (faculty) by their ID */
    private Map<String, AcademicMember> academicMembers = new HashMap<>();

    /** Map of departments by their code */
    private Map<String, Department> departments = new HashMap<>();

    /** Map of academic programs by their code */
    private Map<String, Program> programs = new HashMap<>();

    /** Map of courses by their code */
    private Map<String, Course> courses = new HashMap<>();

    /** Output writer for printing summaries and reports */
    private PrintWriter writer;

    /**
     * Constructs the student management system with a given output writer.
     *
     * @param writer PrintWriter object used for output
     */
    public StudentManagementSystem(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Adds a person (student or faculty) to the system.
     *
     * @param parts String array with person details
     * @throws InvalidPersonTypeException if the person type is invalid
     */
    public void addPerson(String[] parts) throws InvalidPersonTypeException {
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        String email = parts[3];
        String department = parts[4];

        if (type.equals("S")) {
            students.put(id, new Student(id, name, email, department));
        } else if (type.equals("F")) {
            academicMembers.put(id, new AcademicMember(id, name, email, department));
        } else {
            throw new InvalidPersonTypeException("Invalid Person Type");
        }
    }

    /**
     * Adds a department and assigns a head to it.
     *
     * @param parts Department details
     * @throws AcademicMemberNotFoundException if the head's ID is not found
     */
    public void addDepartment(String[] parts) throws AcademicMemberNotFoundException {
        String code = parts[0];
        String name = parts[1];
        String description = parts[2];
        String headId = parts[3];

        Department department = new Department(code, name, description);
        departments.put(code, department);
        if (!academicMembers.containsKey(headId)) {
            throw new AcademicMemberNotFoundException("Academic Member Not Found with ID " + headId);
        }
        department.setHead(academicMembers.get(headId));
    }

    /**
     * Adds an academic program to the system.
     *
     * @param parts Program details
     * @throws DepartmentNotFoundException if the specified department is not found
     */
    public void addProgram(String[] parts) throws DepartmentNotFoundException {
        String code = parts[0];
        String name = parts[1];
        String description = parts[2];
        String departmentName = parts[3];
        String degreeLevel = parts[4];
        int totalCredits = Integer.parseInt(parts[5]);

        Department dept = null;
        for (Department d : departments.values()) {
            if (d.getName().equals(departmentName)) {
                dept = d;
                break;
            }
        }
        if (dept == null) {
            throw new DepartmentNotFoundException("Department " + departmentName + " Not Found");
        }
        programs.put(code, new Program(code, name, description, dept, degreeLevel, totalCredits));
    }

    /**
     * Adds a course to the system and optionally associates it with a program.
     *
     * @param parts Course details
     * @throws ProgramNotFoundException if the specified program code is not found
     */
    public void addCourse(String[] parts) throws ProgramNotFoundException {
        String code = parts[0];
        String name = parts[1];
        String department = parts[2];
        int credits = Integer.parseInt(parts[3]);
        String semester = parts[4];
        String programCode = parts.length > 5 ? parts[5] : null;

        Course course = new Course(code, name, department, credits, semester);

        if (programCode != null) {
            if (!programs.containsKey(programCode)) {
                throw new ProgramNotFoundException("Program " + programCode + " Not Found");
            }
            Program program = programs.get(programCode);
            course.setProgram(program);
            program.addCourse(course);
        }
        courses.put(code, course);
    }

    /**
     * Assigns a course to a student or academic member.
     *
     * @param parts Details for assignment
     * @throws StudentNotFoundException if the student ID is not found
     * @throws AcademicMemberNotFoundException if the academic member ID is not found
     * @throws CourseNotFoundException if the course code is not found
     */
    public void assignCourse(String[] parts) throws StudentNotFoundException, AcademicMemberNotFoundException, CourseNotFoundException {
        String personType = parts[0];
        String id = parts[1];
        String courseCode = parts[2];

        Course course = courses.get(courseCode);

        if (personType.equals("F")) {
            if (!academicMembers.containsKey(id)) {
                throw new AcademicMemberNotFoundException("Academic Member Not Found with ID " + id);
            }
            AcademicMember academicMember = academicMembers.get(id);
            academicMember.assignCourse(course);
            course.assignInstructor(academicMember);
        } else if (personType.equals("S")) {
            if (!students.containsKey(id)) {
                throw new StudentNotFoundException("Student Not Found with ID " + id);
            }
            if (!courses.containsKey(courseCode)) {
                throw new CourseNotFoundException("Course " + courseCode + " Not Found");
            }
            Student student = students.get(id);
            student.enrollCourse(course);
            course.enrollStudent(student);
        } else {
            writer.println("Invalid Person Type");
        }
    }

    /**
     * Assigns a grade to a student for a given course.
     *
     * @param parts Grade assignment details
     * @throws StudentNotFoundException if student not found
     * @throws CourseNotFoundException if course not found
     * @throws InvalidGradeException if grade is invalid
     */
    public void assignGrade(String[] parts) throws StudentNotFoundException, CourseNotFoundException, InvalidGradeException {
        String grade = parts[0];
        String studentId = parts[1];
        String courseCode = parts[2];

        if (!students.containsKey(studentId)) {
            throw new StudentNotFoundException("Student Not Found with ID " + studentId);
        }
        if (!courses.containsKey(courseCode)) {
            throw new CourseNotFoundException("Course " + courseCode + " Not Found");
        }

        Course course = courses.get(courseCode);
        course.getGradeValue(grade);  // Validates the grade

        Student student = students.get(studentId);
        course.assignGrade(student, grade);
        student.completeCourse(course, grade);
    }

    /**
     * Prints summaries of all entities: academic members, students, departments, programs, and courses.
     */
    public void printSummary() {
        // Academic Members
        writer.println("----------------------------------------");
        writer.println("            Academic Members");
        writer.println("----------------------------------------");
        List<AcademicMember> sortedFaculty = new ArrayList<>(academicMembers.values());
        sortedFaculty.sort(Comparator.comparing(AcademicMember::getId));
        for (AcademicMember member : sortedFaculty) {
            writer.println("Faculty ID: " + member.getId());
            writer.println("Name: " + member.getName());
            writer.println("Email: " + member.getEmail());
            writer.println("Department: " + member.getDepartment());
            writer.println();
        }
        writer.println("----------------------------------------\n");

        // Students
        writer.println("----------------------------------------");
        writer.println("                STUDENTS");
        writer.println("----------------------------------------");
        List<Student> sortedStudents = new ArrayList<>(students.values());
        sortedStudents.sort(Comparator.comparing(Student::getId));
        for (Student student : sortedStudents) {
            writer.println("Student ID: " + student.getId());
            writer.println("Name: " + student.getName());
            writer.println("Email: " + student.getEmail());
            writer.println("Major: " + student.getDepartment());
            writer.println("Status: Active");
            writer.println();
        }
        writer.println("----------------------------------------\n");

        // Departments
        writer.println("---------------------------------------");
        writer.println("              DEPARTMENTS");
        writer.println("---------------------------------------");
        List<Department> sortedDepartments = new ArrayList<>(departments.values());
        sortedDepartments.sort(Comparator.comparing(Department::getCode));
        for (Department dept : sortedDepartments) {
            writer.println("Department Code: " + dept.getCode());
            writer.println("Name: " + dept.getName());
            AcademicMember head = dept.getHead();
            writer.println("Head: " + (head != null ? head.getName() : "Not Assigned"));
            writer.println();
        }
        writer.println("----------------------------------------\n");

        // Programs
        writer.println("--------------------------------------");
        writer.println("                PROGRAMS");
        writer.println("--------------------------------------");
        List<Program> sortedPrograms = new ArrayList<>(programs.values());
        sortedPrograms.sort(Comparator.comparing(Program::getCode));
        for (Program prog : sortedPrograms) {
            writer.println("Program Code: " + prog.getCode());
            writer.println("Name: " + prog.getName());
            writer.println("Department: " + prog.getDepartment().getName());
            writer.println("Degree Level: " + prog.getDegreeLevel());
            writer.println("Required Credits: " + prog.getTotalCredits());
            Set<Course> courseSet = new HashSet<>(prog.getCourses());
            List<String> courseCodes = new ArrayList<>();
            for (Course c : courseSet) {
                courseCodes.add(c.getCode());
            }
            Collections.sort(courseCodes);
            writer.println("Courses: " + (courseCodes.isEmpty() ? "-" : "{" + String.join(",", courseCodes) + "}"));
            writer.println();
        }
        writer.println("----------------------------------------\n");

        // Courses
        writer.println("---------------------------------------");
        writer.println("                COURSES");
        writer.println("---------------------------------------");
        List<Course> sortedCourses = new ArrayList<>(courses.values());
        sortedCourses.sort(Comparator.comparing(Course::getCode));
        for (Course course : sortedCourses) {
            writer.println("Course Code: " + course.getCode());
            writer.println("Name: " + course.getName());
            writer.println("Department: " + course.getDepartment());
            writer.println("Credits: " + course.getCredits());
            writer.println("Semester: " + course.getSemester());
            writer.println();
        }
        writer.println("----------------------------------------\n");
    }

    /**
     * Prints detailed reports for all courses including enrolled students and grade distribution.
     *
     * @throws InvalidGradeException if a grade is invalid during calculations
     */
    public void printCourseReports() throws InvalidGradeException {
        writer.println("----------------------------------------");
        writer.println("             COURSE REPORTS");
        writer.println("----------------------------------------");
        List<Course> sortedCourses = new ArrayList<>(courses.values());
        sortedCourses.sort(Comparator.comparing(Course::getCode));

        for (Course course : sortedCourses) {
            writer.println("Course Code: " + course.getCode());
            writer.println("Name: " + course.getName());
            writer.println("Department: " + course.getDepartment());
            writer.println("Credits: " + course.getCredits());
            writer.println("Semester: " + course.getSemester());
            writer.println();
            writer.print("Instructor: ");
            writer.println(course.getInstructor() != null ? course.getInstructor().getName() : "Not Assigned");
            writer.println();

            writer.println("Enrolled Students:");
            List<Student> sortedStudents = new ArrayList<>(course.getGrades().keySet());
            sortedStudents.sort(Comparator.comparing(Student::getId));
            for (Student s : sortedStudents) {
                writer.println("- " + s.getName() + " (ID: " + s.getId() + ")");
            }

            writer.println();
            writer.println("Grade Distribution:");
            List<Map.Entry<String, Integer>> gradeDist = new ArrayList<>(course.getGradeDistribution().entrySet());
            gradeDist.sort(Map.Entry.comparingByKey());
            for (Map.Entry<String, Integer> e : gradeDist) {
                writer.println(e.getKey() + ": " + e.getValue());
            }

            writer.println();
            writer.println("Average Grade: " + String.format("%.2f", course.getAverageGrade()));
            writer.println("----------------------------------------\n");
        }
    }

    /**
     * Prints detailed academic reports for each student including GPA, enrolled and completed courses.
     *
     * @throws InvalidGradeException if an invalid grade is encountered
     */
    public void printStudentReports() throws InvalidGradeException {
        writer.println("----------------------------------------");
        writer.println("            STUDENT REPORTS");
        writer.println("----------------------------------------");
        List<Student> sortedStudents = new ArrayList<>(students.values());
        sortedStudents.sort(Comparator.comparing(Student::getId));

        for (Student student : sortedStudents) {
            writer.println("Student ID: " + student.getId());
            writer.println("Name: " + student.getName());
            writer.println("Email: " + student.getEmail());
            writer.println("Major: " + student.getDepartment());
            writer.println("Status: Active");
            writer.println();

            writer.println("Enrolled Courses:");
            Set<Course> completed = student.getCompletedCourses().keySet();
            List<Course> enrolledCourses = new ArrayList<>();
            for (Course course : student.getEnrolledCourses()) {
                if (!completed.contains(course)) {
                    enrolledCourses.add(course);
                }
            }
            enrolledCourses.sort(Comparator.comparing(Course::getCode));
            for (Course course : enrolledCourses) {
                writer.println("- " + course.getName() + " (" + course.getCode() + ")");
            }

            writer.println();
            writer.println("Completed Courses:");
            List<Map.Entry<Course, String>> completedEntries = new ArrayList<>(student.getCompletedCourses().entrySet());
            completedEntries.sort(Comparator.comparing(entry -> entry.getKey().getCode()));
            for (Map.Entry<Course, String> entry : completedEntries) {
                Course course = entry.getKey();
                String grade = entry.getValue();
                writer.println("- " + course.getName() + " (" + course.getCode() + "): " + grade);
            }

            writer.println();
            writer.println("GPA: " + String.format("%.2f", student.calculateGPA()));
            writer.println("----------------------------------------\n");
        }
    }
}
