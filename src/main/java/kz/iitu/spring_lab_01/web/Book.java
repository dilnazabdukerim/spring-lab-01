package kz.iitu.spring_lab_01.web;

public class Book {

    private Long id;
    private String title;
    private String author;
    private Integer year;
    private Genre genre;
    private boolean available;

    public Book(Long id, String title, String author,
                Integer year, Genre genre, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getYear() {
        return year;
    }

    public Genre getGenre() {
        return genre;
    }

    public boolean isAvailable() {
        return available;
    }
}