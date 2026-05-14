import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // In this part I have matched the arguments with the correct ones
        String itemsFile = args[0];
        String usersFile = args[1];
        String commandsFile = args[2];
        String outputFile = args[3];

        LibraryManagement library = new LibraryManagement(outputFile);
        library.loadItems(itemsFile);
        library.loadUsers(usersFile);
        library.executeCommands(commandsFile);
    }
}


