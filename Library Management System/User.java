import java.util.*;
// Abstract class representing a user of the library
public abstract class User {
    private String id;
    private String name;
    private String phoneNumber;
    private int maxItems;
    private int overdueTime;
    public int penalty = 0;
    public List<LibraryItem> borrowedItems = new ArrayList<>();
    // Constructors
    public User(String name, String id, String phoneNumber){
        this.name = name;
        this.id = id;
        this.phoneNumber = phoneNumber;
    }
    public abstract boolean canBorrow(LibraryItem item);
    // Getters
    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getPhoneNumber(){ return phoneNumber; }
    public int getPenalty() { return penalty; }
    public List<LibraryItem> getBorrowedItems() { return borrowedItems; }
    public int getMaxItems() { return maxItems; }
    public int getOverdueTime() { return overdueTime; }
}


class Student extends User{
    private String faculty;
    private String department;
    private String grade;

    public Student(String name, String id, String phoneNumber, String faculty, String department, String grade) {
        super(name, id, phoneNumber);
        this.faculty = faculty;
        this.department = department;
        this.grade = grade;
    }
    public String getFaculty() { return faculty; }
    public String getDepartment() { return department; }
    public String getGrade() { return grade; }
    public int getMaxItems() { return 5; }
    public int getOverdueTime() { return 30; }

    public boolean canBorrow(LibraryItem item) {
        return borrowedItems.size() <= getMaxItems() && penalty < 6 && !item.getType().equals("reference");
    }
}
class AcademicMember extends User {
    private String faculty;
    private String department;
    private String title;// e.g., Professor, Lecturer

    public AcademicMember(String name, String id, String phoneNumber, String faculty, String department, String title) {
        super(name, id, phoneNumber);
        this.faculty = faculty;
        this.department = department;
        this.title = title;
    }

    public String getFaculty() { return faculty; }
    public String getDepartment() { return department; }
    public String getTitle() { return title; }
    public int getMaxItems() { return 3; }
    public int getOverdueTime() { return 15; }

    public boolean canBorrow(LibraryItem item) {
        return borrowedItems.size() <= getMaxItems() && penalty < 6;
    }
}
class Guest extends User {
    private String occupation;

    public Guest(String name, String id, String phoneNumber, String occupation) {
        super(name, id, phoneNumber);
        this.occupation = occupation;
    }

    public String getOccupation() { return occupation; }
    public int getMaxItems() { return 1; }
    public int getOverdueTime() { return 7; }

    public boolean canBorrow(LibraryItem item) {
        return borrowedItems.size() <= getMaxItems() && penalty < 6 && item.getType().equals("reference") && item.getType().equals("normal");
    }
}

