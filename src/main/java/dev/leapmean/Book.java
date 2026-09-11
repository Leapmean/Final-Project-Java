package dev.leapmean;

public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String registeredDate;

    public Book() {
    }

    public Book(int id, String title, String author, String genre, String registeredDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.registeredDate = registeredDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
