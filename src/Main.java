import java.sql.*;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Library Management System!");

        // Menu will reappear as long as 5 is not selected
        while(true) {
            System.out.println("========================================");
            System.out.println("Enter a number from the following menu:");
            System.out.println("1. Add a book");
            System.out.println("2. Search for a book");
            System.out.println("3. View all books");
            System.out.println("4. Remove a book");
            System.out.println("5. Exit");
            System.out.print("Your choice: ");

            try {
                byte choice = scanner.nextByte();
                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        searchBook();
                        break;
                    case 3:
                        viewAllBooks();
                        break;
                    case 4:
                        removeBook();
                        break;
                    case 5:
                        System.exit(0);
                    default:
                        System.out.println("Invalid entry!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid entry!");
                scanner.nextLine(); // consume invalid entry
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int executeInsertQuery(String query) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";

        try {
            Connection conn = DriverManager.getConnection(url, "admin", "admin");
            PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int m = st.executeUpdate();
            conn.close(); // close connection after every query
            if (m == 0) {
                throw new SQLException("INSERT query failed, no row affected.");
            }

            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("INSERT query failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResultSet executeSelectQuery(String query) throws SQLException{
        String url = "jdbc:postgresql://localhost:5432/postgres";

        try {
            Connection conn = DriverManager.getConnection(url, "admin", "admin");
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery();
            conn.close();
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int executeUpdateQuery(String query) throws SQLException{
        String url = "jdbc:postgresql://localhost:5432/postgres";

        try {
            Connection conn = DriverManager.getConnection(url, "admin", "admin");
            PreparedStatement st = conn.prepareStatement(query);
            return st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addBook() throws SQLException {
        System.out.print("Book title: ");
        scanner.nextLine();
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("Year published: ");
        int yearPublished = scanner.nextInt();
        Book newBook = new Book(title, author, yearPublished);
        newBook.setID(executeInsertQuery(newBook.createInsertQuery()));
        System.out.println(newBook.toString() + " is successfully added to the system!");
    }

    private static void viewAllBooks() throws SQLException {
        ResultSet rs = executeSelectQuery("SELECT * FROM books where active_flag = true");
        System.out.println("ID | Title | Author | Year Published | Date Added ");
        while (rs.next()) {
            int ID = rs.getInt("ID");
            String title = rs.getString("title");
            String author = rs.getString("author");
            int yearPublished = rs.getInt("year_published");
            Date dateAdded = rs.getDate("date_added");
            String record = ID + " | " + title + " | " + author + " | " + yearPublished + " | " + dateAdded;
            System.out.println(record);
        }
    }

    private static void searchBook() throws SQLException {
        System.out.println("Search by ");
        System.out.println("1. ID");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.println("4. Year Published");
        System.out.print("Your choice: ");

        String searchBy = null;
        try {
            byte searchChoice = scanner.nextByte();
            switch (searchChoice) {
                case 1:
                    searchBy = "ID";
                    break;
                case 2:
                    searchBy = "title";
                    break;
                case 3:
                    searchBy = "author";
                    break;
                case 4:
                    searchBy = "year_published";
                    break;
                default:
                    System.out.println("Invalid entry!");
            }

            System.out.print(searchBy + " = ");
            scanner.nextLine();
            String searchVar = scanner.nextLine();

            String searchQuery = String.format("SELECT * FROM books where %s = '%s' and active_flag = true", searchBy, searchVar);
            ResultSet rs = executeSelectQuery(searchQuery);

            if (!rs.isBeforeFirst()) {
                System.out.println("No entry found.");
            } else {
                System.out.println("ID | Title | Author | Year Published | Date Added");
                while (rs.next()) {
                    int ID = rs.getInt("ID");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int yearPublished = rs.getInt("year_published");
                    Date dateAdded = rs.getDate("date_added");
                    String record = ID + " | " + title + " | " + author + " | " + yearPublished + " | " + dateAdded;
                    System.out.println(record);
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid entry! Please enter a number between 1-4!");
            scanner.nextLine(); // consume invalid entry
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // Remove a book by setting its active_flag to false
    private static void removeBook() throws SQLException {
        System.out.print("Please enter ID of book to be removed: ");
        int bookID = scanner.nextInt();
        int m = executeUpdateQuery(String.format("UPDATE books SET active_flag = false WHERE ID = %s", bookID));

        if (m != 0) {
            System.out.printf("%s has been removed successfully.", bookID);
        } else {
            System.out.println("Removal failed!");
        }

    }
}