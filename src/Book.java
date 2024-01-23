public class Book {
    private String title;
    private String author;
    private int yearPublished;
    private int ID ;

    public Book(String title, String author, int yearPublished) {
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
    }

    public Book(int ID, String title, String author, int yearPublished) {
        this.title = title;
        this.author = author;
        this.yearPublished = yearPublished;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String createInsertQuery() {
        return "INSERT INTO books (title, author, year_published) VALUES ('" +
                title + "', " + "'" + author + "', " + yearPublished + ");";
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year published=" + yearPublished +
                ", ID=" + ID +
                '}';
    }
}
