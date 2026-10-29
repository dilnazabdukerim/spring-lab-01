package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.domain.Genre;
import kz.iitu.spring_lab_01.entity.Book;
import kz.iitu.spring_lab_01.service.BookService;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService bookService;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "year", "genre", "createdAt");

    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    // Task 4.1: pagination and sorting
    @GetMapping
    public Page<Book> findAll(
            @RequestParam(required = false) Genre genre,
            @ParameterObject
            @PageableDefault(size = 5, sort = "title")
            Pageable pageable
    ) {
        if (genre != null) {
            return bookService.findByGenre(genre, pageable);
        }

        return bookService.findAll(pageable);
    }

    // Get a book by ID
    @GetMapping("/{id}")
    public Book findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    // Create a book
    @PostMapping
    public Book create(@RequestBody Book book) {
        return bookService.create(book);
    }

    // Update a book
    @PutMapping("/{id}")
    public Book update(
            @PathVariable Long id,
            @RequestBody Book book
    ) {
        return bookService.update(id, book);
    }

    // Delete a book
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    // Find books by author
    @GetMapping("/author/{authorName}")
    public List<Book> findByAuthor(
            @PathVariable String authorName
    ) {
        return bookService.findByAuthor(authorName);
    }

    // Find books by genre
    @GetMapping("/genre/{genre}")
    public List<Book> findByGenre(@PathVariable Genre genre) {
        return bookService.findByGenre(genre);
    }

    // Find active books
    @GetMapping("/active")
    public List<Book> findActiveBooks() {
        return bookService.findActiveBooks();
    }

    // Find active books by genre
    @GetMapping("/active/genre/{genre}")
    public List<Book> findActiveBooksByGenre(
            @PathVariable Genre genre
    ) {
        return bookService.findActiveBooksByGenre(genre);
    }

    // Additional pagination endpoint with validated sorting
    @GetMapping("/page")
    public Page<Book> findAllPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be 0 or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        // Only allow known entity properties
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field. Allowed fields: "
                            + ALLOWED_SORT_FIELDS
            );
        }

        Sort.Direction sortDirection;

        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException(
                    "Direction must be asc or desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return bookService.findAll(pageable);
    }

    // Search books by title
    @GetMapping("/search/title")
    public List<Book> searchByTitle(
            @RequestParam String title
    ) {
        return bookService.searchByTitle(title);
    }

    // Search books by year range
    @GetMapping("/search/year")
    public List<Book> findByYearRange(
            @RequestParam int startYear,
            @RequestParam int endYear
    ) {
        return bookService.findByYearRange(startYear, endYear);
    }

    // Get the three newest books of a genre
    @GetMapping("/top3/{genre}")
    public List<Book> findTop3ByGenre(
            @PathVariable Genre genre
    ) {
        return bookService.findTop3ByGenre(genre);
    }

    // Count books by author
    @GetMapping("/count/author")
    public long countByAuthor(
            @RequestParam String name
    ) {
        return bookService.countByAuthor(name);
    }

    // JPQL: search by genre and year range
    @GetMapping("/search/genre-year")
    public List<Book> findByGenreAndYearRange(
            @RequestParam Genre genre,
            @RequestParam int startYear,
            @RequestParam int endYear
    ) {
        return bookService.findByGenreAndYearRange(
                genre,
                startYear,
                endYear
        );
    }

    // Archive books by year
    @PutMapping("/archive/year/{year}")
    public String archiveBooksByYear(
            @PathVariable int year
    ) {
        int count = bookService.archiveBooksByYear(year);
        return "Archived books: " + count;
    }

    // Task 3.4: N+1 before optimization
    @GetMapping("/n-plus-one/before")
    public List<String> testNPlusOneBefore() {
        return bookService.getAuthorsWithoutFetch();
    }

    // Task 3.4: N+1 after optimization
    @GetMapping("/n-plus-one/after")
    public List<Book> testNPlusOneAfter() {
        return bookService.findAll();
    }
}
