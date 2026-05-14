/**
 * Represents an academic department in the university.
 * A department has a unique code, name, description, and a head (an academic member).
 */
public class Department {
    /** Unique code identifying the department */
    private String code;

    /** Name of the department */
    private String name;

    /** Description of the department’s academic focus or scope */
    private String description;

    /** The academic member assigned as the head of the department */
    private AcademicMember head;

    /**
     * Constructs a Department with the specified code, name, and description.
     *
     * @param code        Unique code of the department
     * @param name        Name of the department
     * @param description Brief description of the department
     */
    public Department(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * Assigns an academic member as the head of the department.
     *
     * @param head Academic member to be set as department head
     */
    public void setHead(AcademicMember head) {
        this.head = head;
    }

    /**
     * @return Unique code of the department
     */
    public String getCode() { return code; }

    /**
     * @return Name of the department
     */
    public String getName() { return name; }

    /**
     * @return Description of the department
     */
    public String getDescription() { return description; }

    /**
     * @return Academic member who is head of the department
     */
    public AcademicMember getHead() { return head; }
}

