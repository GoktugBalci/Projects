// Base class representing a library item
public class LibraryItem {
    private String id;
    private String title;
    private String type;

    //Constructors
    public LibraryItem(String id, String title, String type){
        this.id = id;
        this.title = title;
        this.type =type;
    }
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
}
class Book extends LibraryItem {
    private String author;
    private String genre;


    public Book(String id, String title, String author, String genre, String type) {
        super(id, title, type);
        this.author = author;
        this.genre = genre;
    }

    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
}
class Magazine extends LibraryItem {
    private String publisher;
    private String category;

    public Magazine(String id, String title, String publisher, String category, String type) {
        super(id, title, type);
        this.publisher = publisher;
        this.category = category;
    }

    public String getPublisher() { return publisher; }
    public String getCategory() { return category; }


    }
class DVD extends LibraryItem{
    private String director;
    private String category;
    private String runtime;

    public DVD(String id, String title, String director, String category, String runtime, String type){
        super(id, title, type);
        this.director = director;
        this.category = category;
        this.runtime = runtime;
    }
    public String getDirector() { return director; }
    public String getCategory() { return category; }
    public String getRuntime() { return runtime; }
}

