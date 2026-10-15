package kz.iitu.spring_lab_01.web;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookService {

    private final List<Book> books = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Book create(BookForm form) {
        Book book = new Book(
                nextId.getAndIncrement(),
                form.getTitle(),
                form.getAuthor(),
                form.getYear(),
                form.getGenre(),
                form.isAvailable()
        );

        books.add(book);
        return book;
    }

    public List<Book> findAll() {
        return new ArrayList<>(books);
    }
}