package assign1;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class Book {
    private String title;
    private String type;
    
    public Book(String title, String type) {
        this.title = title;
        this.type = type;
    }
    
    public String getTitle() { return title; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return title + " (" + type + ")";
    }
}

class Author {
    private String name;
    
    public Author(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    
    @Override
    public String toString() {
        return name;
    }
}

class BookAuthorPair {
    private Book book;
    private Author author;
    
    public BookAuthorPair(Book book, Author author) {
        this.book = book;
        this.author = author;
    }
    
    public Book getBook() { return book; }
    public Author getAuthor() { return author; }
    
    @Override
    public String toString() {
        return String.format("%-25s | %-20s | %-15s", 
                           book.getTitle(), author.getName(), book.getType());
    }
}

public class assign1_4 {
    public static void main(String[] args) {
        // Create sample authors
        Author[] authors = {
            new Author("J.K. Rowling"),
            new Author("George R.R. Martin"),
            new Author("Stephen King"),
            new Author("J.R.R. Tolkien"),
            new Author("Agatha Christie"),
            new Author("Isaac Asimov"),
            new Author("Frank Herbert"),
            new Author("Dan Brown"),
            new Author("Jane Austen"),
            new Author("Ernest Hemingway")
        };
        
        // Create sample books with types
        Book[] books = {
            new Book("Harry Potter", "Fantasy"),
            new Book("A Game of Thrones", "Fantasy"),
            new Book("The Shining", "Horror"),
            new Book("The Lord of the Rings", "Fantasy"),
            new Book("Murder on the Orient Express", "Mystery"),
            new Book("Foundation", "Sci-Fi"),
            new Book("Dune", "Sci-Fi"),
            new Book("The Da Vinci Code", "Mystery"),
            new Book("Pride and Prejudice", "Classic"),
            new Book("The Old Man and the Sea", "Classic"),
            new Book("The Stand", "Horror"),
            new Book("I, Robot", "Sci-Fi")
        };
        
        // Create book-author pairs
        List<BookAuthorPair> pairs = Arrays.asList(
            new BookAuthorPair(books[0], authors[0]),   // Harry Potter - Rowling
            new BookAuthorPair(books[1], authors[1]),   // Game of Thrones - Martin
            new BookAuthorPair(books[2], authors[2]),   // The Shining - King
            new BookAuthorPair(books[3], authors[3]),   // LOTR - Tolkien
            new BookAuthorPair(books[4], authors[4]),   // Murder - Christie
            new BookAuthorPair(books[5], authors[5]),   // Foundation - Asimov
            new BookAuthorPair(books[6], authors[6]),   // Dune - Herbert
            new BookAuthorPair(books[7], authors[7]),   // Da Vinci Code - Brown
            new BookAuthorPair(books[8], authors[8]),   // Pride - Austen
            new BookAuthorPair(books[9], authors[9]),   // Old Man - Hemingway
            new BookAuthorPair(books[10], authors[2]),  // The Stand - King
            new BookAuthorPair(books[11], authors[5])   // I, Robot - Asimov
        );
        
        System.out.println("=== UNSORTED BOOK LIST ===");
        System.out.println(String.format("%-25s | %-20s | %-15s", 
                                       "BOOK TITLE", "AUTHOR", "TYPE"));
        System.out.println("-------------------------------------------------------------");
        pairs.forEach(System.out::println);
        
        // Sort by: 1. Book type, 2. Author name, 3. Book name
        pairs.sort(Comparator
            .comparing((BookAuthorPair pair) -> pair.getBook().getType())
            .thenComparing(pair -> pair.getAuthor().getName())
            .thenComparing(pair -> pair.getBook().getTitle())
        );
        
        System.out.println("\n=== SORTED BOOK LIST (by Type > Author > Title) ===");
        System.out.println(String.format("%-25s | %-20s | %-15s", 
                                       "BOOK TITLE", "AUTHOR", "TYPE"));
        System.out.println("-------------------------------------------------------------");
        pairs.forEach(System.out::println);
        
        // Display by category
        System.out.println("\n=== BOOKS GROUPED BY TYPE ===");
        String currentType = "";
        for (BookAuthorPair pair : pairs) {
            if (!pair.getBook().getType().equals(currentType)) {
                currentType = pair.getBook().getType();
                System.out.println("\n--- " + currentType + " ---");
            }
            System.out.println("  " + pair.getAuthor().getName() + " - " + pair.getBook().getTitle());
        }
    }
}