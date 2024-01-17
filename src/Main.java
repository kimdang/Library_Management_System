import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Library Management System!");

        // Menu will reappear as long as 4 is not selected
        while(true) {
            System.out.println("========================================");
            System.out.println("Enter a number from the following menu:");
            System.out.println("1. Add a book");
            System.out.println("2. Search for a book");
            System.out.println("3. View all books");
            System.out.println("4. Exit");
            System.out.print("Your choice: ");

            try {
                byte choice = scanner.nextByte();

                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        System.out.println("2");
                        break;
                    case 3:
                        System.out.println("3");
                    case 4:
                        System.exit(0);
                    default:
                        System.out.println("Invalid entry!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid entry! Please enter a number!");
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

    private static void executeSelectQuery(String query) throws SQLException{
        String url = "jdbc:postgresql://localhost:5432/postgres";

        try {
            Connection conn = DriverManager.getConnection(url, "admin", "admin");
            PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);


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
        System.out.println(executeSelectQuery("SELECT * FROM books"));
    }


}