package kz.iitu.spring_lab_01.service;

public class DuplicateTitleException extends RuntimeException {

    private final String title;

    public DuplicateTitleException(String title) {
        super("A book with this title and author already exists: " + title);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}