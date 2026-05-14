import java.io.*;
import java.util.*;

/**
 * The {@code Main} class serves as the entry point for the Student Management System.
 * It reads input data from various files, processes them using the {@code StudentManagementSystem},
 * and writes reports to an output file.
 */
public class Main {
    /**
     * Main method to run the Student Management System.
     *
     * @param args Command line arguments in the following order:
     *             [0] personFile, [1] departmentFile, [2] programFile,
     *             [3] courseFile, [4] assignmentFile, [5] gradesFile, [6] outputFile
     */
    public static void main(String[] args) {
        String personFile = args[0];
        String departmentFile = args[1];
        String programFile = args[2];
        String courseFile = args[3];
        String assignmentFile = args[4];
        String gradesFile = args[5];
        String outputFile = args[6];

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            StudentManagementSystem sms = new StudentManagementSystem(writer);

            writer.println("Reading Person Information");
            readPersons(personFile, sms, writer);

            writer.println("Reading Departments");
            readDepartments(departmentFile, sms, writer);

            writer.println("Reading Programs");
            readPrograms(programFile, sms, writer);

            writer.println("Reading Courses");
            readCourses(courseFile, sms, writer);

            writer.println("Reading Course Assignments");
            readAssignments(assignmentFile, sms, writer);

            writer.println("Reading Grades");
            readGrades(gradesFile, sms, writer);

            sms.printSummary();
            sms.printCourseReports();
            sms.printStudentReports();

        } catch (IOException | InvalidGradeException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    /**
     * Reads person data from a file and adds each person to the system.
     *
     * @param input  Path to the person file
     * @param sms    The student management system
     * @param writer Writer for logging
     */
    private static void readPersons(String input, StudentManagementSystem sms, PrintWriter writer) {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) continue;
                String[] parts = line.split(",");
                try {
                    sms.addPerson(parts);
                } catch (InvalidPersonTypeException e) {
                    writer.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + input);
        }
    }
    /**
     * Reads department data from a file and adds each department to the system.
     *
     * @param input  Path to the department file
     * @param sms    The student management system
     * @param writer Writer for logging
     */
    private static void readDepartments(String input, StudentManagementSystem sms, PrintWriter writer) {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) continue;
                String[] parts = line.split(",");
                try {
                    sms.addDepartment(parts);
                } catch (AcademicMemberNotFoundException e) {
                    writer.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + input);
        }
    }
    /**
     * Reads program data from a file and adds each program to the system.
     *
     * @param input  Path to the program file
     * @param sms    The student management system
     * @param writer Writer for logging
     */
    private static void readPrograms(String input, StudentManagementSystem sms, PrintWriter writer) {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) continue;
                String[] parts = line.split(",");
                try {
                    sms.addProgram(parts);
                } catch (DepartmentNotFoundException e) {
                    writer.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + input);
        }
    }
    /**
     * Reads course data from a file and adds each course to the system.
     *
     * @param input  Path to the course file
     * @param sms    The student management system
     * @param writer Writer for logging
     */
    private static void readCourses(String input, StudentManagementSystem sms, PrintWriter writer) {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) continue;
                String[] parts = line.split(",");
                try {
                    sms.addCourse(parts);
                } catch (ProgramNotFoundException e) {
                    writer.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + input);
        }
    }
    /**
     * Reads course assignment data and assigns courses to students or academic members.
     *
     * @param input  Path to the assignment file
     * @param sms    The student management system
     * @param writer Writer for logging
     */
    private static void readAssignments(String input, StudentManagementSystem sms, PrintWriter writer) {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) continue;
                String[] parts = line.split(",");
                try {
                    sms.assignCourse(parts);
                } catch (StudentNotFoundException | AcademicMemberNotFoundException | CourseNotFoundException e) {
                    writer.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + input);
        }
    }
    /**
     * Reads grade data from a file and assigns grades to students.
     *
     * @param input  Path to the grades file
     * @param sms    The student management system
     * @param writer Writer for logging
     */
    private static void readGrades(String input, StudentManagementSystem sms, PrintWriter writer) {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) continue;
                String[] parts = line.split(",");
                try {
                    sms.assignGrade(parts);
                } catch (StudentNotFoundException | CourseNotFoundException | InvalidGradeException e) {
                    writer.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("File not found: " + input);
        }
    }
}
