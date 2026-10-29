package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.domain.Genre;
import kz.iitu.spring_lab_01.entity.Author;
import kz.iitu.spring_lab_01.entity.Book;
import kz.iitu.spring_lab_01.entity.Tag;
import kz.iitu.spring_lab_01.repository.AuthorRepository;
import kz.iitu.spring_lab_01.repository.BookRepository;
import kz.iitu.spring_lab_01.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    public BookService(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            TagRepository tagRepository
    ) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    // Получение всех книг
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAllWithAuthors();
    }

    // Получение книг с пагинацией
    @Transactional(readOnly = true)
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAllWithDetails(pageable);
    }

    // Получение книги по ID
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with id: " + id
                        ));
    }

    // Создание книги
    public Book create(Book book) {
        if (book.getAuthor() == null
                || book.getAuthor().getName() == null
                || book.getAuthor().getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Author name is required"
            );
        }

        String authorName = book.getAuthor().getName().trim();

        Author author = authorRepository
                .findByNameIgnoreCase(authorName)
                .orElseGet(() ->
                        authorRepository.save(
                                new Author(authorName)
                        ));

        book.setAuthor(author);
        book.setTags(resolveTags(book.getTags()));

        return bookRepository.save(book);
    }

    // Обновление книги
    public Book update(Long id, Book updatedBook) {
        Book existingBook = findById(id);

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setYear(updatedBook.getYear());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setArchived(updatedBook.isArchived());

        if (updatedBook.getAuthor() != null
                && updatedBook.getAuthor().getName() != null
                && !updatedBook.getAuthor().getName().isBlank()) {

            String authorName =
                    updatedBook.getAuthor().getName().trim();

            Author author = authorRepository
                    .findByNameIgnoreCase(authorName)
                    .orElseGet(() ->
                            authorRepository.save(
                                    new Author(authorName)
                            ));

            existingBook.setAuthor(author);
        }

        if (updatedBook.getTags() != null) {
            existingBook.setTags(
                    resolveTags(updatedBook.getTags())
            );
        }

        return bookRepository.save(existingBook);
    }

    // Поиск или создание тегов
    private Set<Tag> resolveTags(Set<Tag> requestedTags) {
        Set<Tag> resolvedTags = new HashSet<>();

        if (requestedTags == null) {
            return resolvedTags;
        }

        for (Tag requestedTag : requestedTags) {
            if (requestedTag.getName() == null
                    || requestedTag.getName().isBlank()) {
                throw new IllegalArgumentException(
                        "Tag name is required"
                );
            }

            String tagName = requestedTag.getName().trim();

            Tag tag = tagRepository
                    .findByNameIgnoreCase(tagName)
                    .orElseGet(() ->
                            tagRepository.save(
                                    new Tag(tagName)
                            ));

            resolvedTags.add(tag);
        }

        return resolvedTags;
    }

    // Удаление книги
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }

    // Поиск книг по имени автора
    @Transactional(readOnly = true)
    public List<Book> findByAuthor(String authorName) {
        return bookRepository
                .findByAuthor_NameIgnoreCase(authorName);
    }

    // Поиск книг по жанру
    @Transactional(readOnly = true)
    public List<Book> findByGenre(Genre genre) {
        return bookRepository.findByGenre(genre);
    }

    // Получение неархивных книг
    @Transactional(readOnly = true)
    public List<Book> findActiveBooks() {
        return bookRepository.findByArchivedFalse();
    }

    // Получение неархивных книг по жанру
    @Transactional(readOnly = true)
    public List<Book> findActiveBooksByGenre(Genre genre) {
        return bookRepository.findActiveBooksByGenre(genre);
    }

    // Derived query: поиск по части названия без учёта регистра
    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository
                .findByTitleContainingIgnoreCase(title);
    }

    // Derived query: поиск книг в диапазоне годов
    @Transactional(readOnly = true)
    public List<Book> findByYearRange(
            int startYear,
            int endYear
    ) {
        if (startYear > endYear) {
            throw new IllegalArgumentException(
                    "startYear must not be greater than endYear"
            );
        }

        return bookRepository
                .findByYearBetweenOrderByYearDesc(
                        startYear,
                        endYear
                );
    }

    // Derived query: три самые новые книги указанного жанра
    @Transactional(readOnly = true)
    public List<Book> findTop3ByGenre(Genre genre) {
        return bookRepository
                .findTop3ByGenreOrderByYearDesc(genre);
    }

    // Derived query: количество книг автора
    @Transactional(readOnly = true)
    public long countByAuthor(String authorName) {
        return bookRepository
                .countByAuthor_NameIgnoreCase(authorName);
    }

    // JPQL: поиск по жанру и диапазону годов
    @Transactional(readOnly = true)
    public List<Book> findByGenreAndYearRange(
            Genre genre,
            int startYear,
            int endYear
    ) {
        if (startYear > endYear) {
            throw new IllegalArgumentException(
                    "startYear must not be greater than endYear"
            );
        }

        return bookRepository.findByGenreAndYearRange(
                genre,
                startYear,
                endYear
        );
    }

    // JPQL: архивирование одной книги
    public int archiveBook(Long id) {
        return bookRepository.archiveBook(id);
    }

    // JPQL: массовое архивирование книг за указанный год
    public int archiveBooksByYear(int year) {
        return bookRepository.archiveBooksByYear(year);
    }

    // Task 3.4: demonstrate N+1 before optimization
    @Transactional(readOnly = true)
    public List<String> getAuthorsWithoutFetch() {
        return bookRepository.findAllWithoutFetch()
                .stream()
                .map(book -> book.getTitle() + " — "
                        + book.getAuthor().getName())
                .toList();
    }
    @Transactional(readOnly = true)
    public Page<Book> findByGenre(Genre genre, Pageable pageable) {
        return bookRepository.findByGenre(genre, pageable);
    }
}