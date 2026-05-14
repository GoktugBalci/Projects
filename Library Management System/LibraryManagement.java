import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LibraryManagement {
    private Map<String, User> users; // Stores the users
    private Map<String, LibraryItem> items; // Stores the items
    private BufferedWriter outputWriter; // For be able to write to output file
    private Map<LibraryItem, LocalDate> borrowDates = new HashMap<>(); // Be able to keep the borrow dates

    List<LibraryItem> unavailableItems = new ArrayList<>(); // List of items that are borrowed so unavailable
    public LibraryManagement(String outputFile) throws IOException {
        this.users = new HashMap<>();
        this.items = new HashMap<>();
        this.outputWriter = new BufferedWriter(new FileWriter(outputFile));
    }

    // Method to write to output
    private void writeToOutput(String message) throws IOException {
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    // In this part I took items from a file
    public void loadItems(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String itemClass = parts[0];
                //I created different types of library items and add them to the map
                switch (itemClass) {
                    case "B": {
                        String id = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        String genre = parts[4];
                        String type = parts[5];

                        Book book = new Book(id, title, author, genre, type);
                        items.put(id, book);
                        break;
                    }
                    case "M": {
                        String id = parts[1];
                        String title = parts[2];
                        String publisher = parts[3];
                        String category = parts[4];
                        String type = parts[5];

                        Magazine magazine = new Magazine(id, title, publisher, category, type);
                        items.put(id, magazine);
                        break;
                    }
                    case "D": {
                        String id = parts[1];
                        String title = parts[2];
                        String director = parts[3];
                        String category = parts[4];
                        String runtime = parts[5];
                        String type = parts[6];

                        DVD dvd = new DVD(id, title, director, category, runtime, type);
                        items.put(id, dvd);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            writeToOutput("Error loading items: " + e.getMessage());
        }
    }
    // In this part I took users from a file
    public void loadUsers(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String userClass = parts[0];
                //I created different types of library items and add them to the map
                switch (userClass) {
                    case "S": { // Student
                        String name = parts[1];
                        String id = parts[2];
                        String phoneNumber = parts[3];
                        String department = parts[4];
                        String faculty = parts[5];
                        String grade = parts[6];

                        Student student = new Student(id, name, phoneNumber, department, faculty, grade);
                        users.put(id, student);
                        break;
                    }
                    case "A": { // Academic Member
                        String name = parts[1];
                        String id = parts[2];
                        String phoneNumber = parts[3];
                        String department = parts[4];
                        String faculty = parts[5];
                        String title = parts[6];

                        AcademicMember academicMember = new AcademicMember(id, name, phoneNumber, department, faculty, title);
                        users.put(id, academicMember);
                        break;
                    }
                    case "G": { // Guest
                        String name = parts[1];
                        String id = parts[2];
                        String phoneNumber = parts[3];
                        String occupation = parts[4];

                        Guest guest = new Guest(id, name, phoneNumber, occupation);
                        users.put(id, guest);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            writeToOutput("Error loading users: " + e.getMessage());
        }
    }
    // In this part I executed the commands as what they are on the file next
    public void executeCommands(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String command = parts[0];

                switch (command) {
                    case "borrow": {
                        String userId = parts[1];
                        String itemId = parts[2];
                        String date = parts[3];

                        borrowItem(userId, itemId, date);
                        break;
                    }
                    case "return": {
                        String userId = parts[1];
                        String itemId = parts[2];

                        returnItem(userId, itemId);
                        break;
                    }
                    case "displayUsers":
                        displayUsers();
                        break;
                    case "displayItems":
                        displayItems();
                        break;
                    case "pay": {
                        String userId = parts[1];
                        payPenalty(userId);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            writeToOutput("Error processing commands: " + e.getMessage());
        }
    }
    // It's the method of borrowing
    private void borrowItem(String userId, String itemId, String dateStr) throws IOException {
        User user = users.get(userId);
        LibraryItem item = items.get(itemId);
        // In this part I took the difference of two dates
        String[] parts = dateStr.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        LocalDate date1  = LocalDate.of(year, month, day);
        LocalDate date2 = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(date1,date2);

        if (unavailableItems.contains(item)) {
            writeToOutput(user.getId() + " cannot borrow " + item.getTitle() + ", it is not available!");
        } else if (!user.canBorrow(item)){
            if (user.getPenalty() >= 6){// For people who can't borrow because of penalty
                writeToOutput(user.getId() + " cannot borrow " + item.getTitle() + ", you must first pay the penalty amount! 6$");
            } else if (user instanceof Student && item.getType().equalsIgnoreCase("reference")) { // For students who can't borrow reference items
                writeToOutput(user.getId() + " cannot borrow reference item!");
            } else if (user instanceof Guest && (item.getType().equalsIgnoreCase("rare") || item.getType().equalsIgnoreCase("limited"))) { // For Guests who can't borrow limited and rare items
                writeToOutput(user.getId() + " cannot borrow " + item.getType() + " item!");
            }
        } else if (user.getBorrowedItems().size() >= user.getMaxItems()) { // For people who are not able to borrow because of borrow limitations
            writeToOutput(user.getId() + " cannot borrow " + item.getTitle() + ", since the borrow limit has been reached!");
        } else if(user.canBorrow(item)){
            unavailableItems.add(item);
            user.borrowedItems.add(item);
            borrowDates.put(item, date1);
            writeToOutput(user.getId() + " successfully borrowed! " + item.getTitle());
        }
        // I gave penalty to users which didn't return the bok they took before 30 days ago
        if (daysBetween >= user.getOverdueTime()){
            user.borrowedItems.remove(item);
            borrowDates.remove(item);
            unavailableItems.remove(item);
            user.penalty += 2;
        }
    }
    // Method of returning the book to library
    private void returnItem(String userId, String itemId) throws IOException {
        User user = users.get(userId);
        LibraryItem item = items.get(itemId);

        unavailableItems.remove(item);
        user.borrowedItems.remove(item);
        borrowDates.remove(item);
        writeToOutput(user.getId() + " successfully returned " + item.getTitle());
    }
    // Method of paying the penalty
    private void payPenalty(String userId) throws IOException {
        User user = users.get(userId);
        user.penalty = 0;
        writeToOutput(user.getId() + " has paid penalty");
    }
    // In this part I sorted the users ascending order with their ID's and gave their another infos
    private void displayUsers() throws IOException {
        List<User> sortedUsers = new ArrayList<>(users.values());
        sortedUsers.sort(Comparator.comparing(User::getName));

        writeToOutput("");
        writeToOutput("");
        for (User user : sortedUsers){
            writeToOutput("------ User Information for " + user.getName() + " ------");
            if(user instanceof AcademicMember){
                writeToOutput("Name: " + ((AcademicMember) user).getTitle() + " " + user.getId() + " Phone: " + user.getPhoneNumber());
            }else {
                writeToOutput("Name: " + user.getId() + " Phone: " + user.getPhoneNumber());
            }

            if (user instanceof Student) {
                Student s = (Student) user;
                writeToOutput("Faculty: " + s.getDepartment() + " Department: " + s.getFaculty() + " Grade: " + s.getGrade() + "th");
            } else if (user instanceof AcademicMember) {
                AcademicMember a = (AcademicMember) user;
                writeToOutput("Faculty: " + a.getDepartment() + " Department: " + a.getFaculty());
            } else if (user instanceof Guest) {
                Guest g = (Guest) user;
                writeToOutput("Occupation: " + g.getOccupation());
            }

            if (user.getPenalty() > 0) {
                writeToOutput("Penalty: $" + user.getPenalty());
            }

            writeToOutput("");
        }
    }
    // In this part I ordered the items by their ID's in ascending order
    private void displayItems() throws IOException {
        List<LibraryItem> sortedItems = new ArrayList<>(items.values());
        sortedItems.sort(Comparator.comparing(LibraryItem::getId));

        for (LibraryItem item : sortedItems) {
            writeToOutput("");
            writeToOutput("------ Item Information for " + item.getId() + " ------");

            boolean isBorrowed = unavailableItems.contains(item);
            String statusLine = "ID: " + item.getId() + " Name: " + item.getTitle();

            if (isBorrowed) {
                LocalDate borrowDate = borrowDates.get(item);
                String borrowerName = "";
                for (User user : users.values()) {
                    if (user.getBorrowedItems().contains(item)) {
                        borrowerName = user.getId();
                        break;
                    }
                }
                String formattedDate = String.format("%02d/%02d/%d", borrowDate.getDayOfMonth(), borrowDate.getMonthValue(), borrowDate.getYear());
                statusLine += " Status: Borrowed Borrowed Date: " + formattedDate + " Borrowed by: " + borrowerName;
            } else {
                statusLine += " Status: Available";
            }

            writeToOutput(statusLine);

            if (item instanceof Book) {
                Book b = (Book) item;
                writeToOutput("Author: " + b.getAuthor() + " Genre: " + b.getGenre());
            } else if (item instanceof Magazine) {
                Magazine m = (Magazine) item;
                writeToOutput("Publisher: " + m.getPublisher() + " Category: " + m.getCategory());
            } else if (item instanceof DVD) {
                DVD d = (DVD) item;
                writeToOutput("Director: " + d.getDirector() + " Category: " + d.getCategory() + " Runtime: " + d.getRuntime());
            }
        }
    }
}


