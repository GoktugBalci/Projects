/**
 * Thrown when an invalid person type is provided during person creation.
 */
class InvalidPersonTypeException extends Exception {
    public InvalidPersonTypeException(String message) {
        super(message);
    }
}

/**
 * Thrown when a student with the given ID is not found in the system.
 */
class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}

/**
 * Thrown when an academic member with the given ID is not found in the system.
 */
class AcademicMemberNotFoundException extends Exception {
    public AcademicMemberNotFoundException(String message) {
        super(message);
    }
}

/**
 * Thrown when a specified department is not found in the system.
 */
class DepartmentNotFoundException extends Exception {
    public DepartmentNotFoundException(String message) {
        super(message);
    }
}

/**
 * Thrown when a specified academic program is not found in the system.
 */
class ProgramNotFoundException extends Exception {
    public ProgramNotFoundException(String message) {
        super(message);
    }
}

/**
 * Thrown when a specified course is not found in the system.
 */
class CourseNotFoundException extends Exception {
    public CourseNotFoundException(String message) {
        super(message);
    }
}

/**
 * Thrown when an invalid grade is assigned to a student.
 */
class InvalidGradeException extends Exception {
    public InvalidGradeException(String message) {
        super(message);
    }
}
