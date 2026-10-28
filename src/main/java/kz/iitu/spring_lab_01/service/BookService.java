package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookService {

    private final List<Book> books = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return new ArrayList<>(books);
        }

        return books.stream()
                .filter(book -> book.author().equalsIgnoreCase(author))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return books.stream()
                .filter(book -> book.id() == id)
                .findFirst();
    }

    public Book create(Book book) {
        boolean duplicate = books.stream()
                .anyMatch(existing ->
                        existing.title().equalsIgnoreCase(book.title())
                                && existing.author().equalsIgnoreCase(book.author()));

        if (duplicate) {
            throw new DuplicateTitleException(book.title());
        }

        Book saved = new Book(
                nextId.getAndIncrement(),
                book.title(),
                book.author(),
                book.year(),
                book.genre(),
                book.createdAt()
        );

        books.add(saved);
        return saved;
    }

    public Book update(long id, Book updatedBook) {
        Book existingBook = findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        boolean duplicate = books.stream()
                .anyMatch(book ->
                        book.id() != id
                                && book.title().equalsIgnoreCase(updatedBook.title())
                                && book.author().equalsIgnoreCase(updatedBook.author()));

        if (duplicate) {
            throw new DuplicateTitleException(updatedBook.title());
        }

        Book saved = new Book(
                existingBook.id(),
                updatedBook.title(),
                updatedBook.author(),
                updatedBook.year(),
                updatedBook.genre(),
                existingBook.createdAt()
        );

        books.set(books.indexOf(existingBook), saved);

        return saved;
    }

    public void delete(long id) {
        boolean removed = books.removeIf(book -> book.id() == id);

        if (!removed) {
            throw new BookNotFoundException(id);
        }
    }
}