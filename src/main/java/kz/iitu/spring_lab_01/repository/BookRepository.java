package kz.iitu.spring_lab_01.repository;

import kz.iitu.spring_lab_01.domain.Genre;
import kz.iitu.spring_lab_01.entity.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Find books by author name
    List<Book> findByAuthor_NameIgnoreCase(String authorName);

    // Find books by genre
    List<Book> findByGenre(Genre genre);

    // Find books by genre with pagination
    Page<Book> findByGenre(Genre genre, Pageable pageable);

    // Find books that are not archived
    List<Book> findByArchivedFalse();

    // Find books by part of title
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Find books between years, ordered by year descending
    List<Book> findByYearBetweenOrderByYearDesc(int startYear, int endYear);

    // Find top 3 newest books by genre
    List<Book> findTop3ByGenreOrderByYearDesc(Genre genre);

    // Count books by author name
    long countByAuthor_NameIgnoreCase(String authorName);


    // Find all books with authors and tags loaded
    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("SELECT DISTINCT b FROM Book b")
    List<Book> findAllWithAuthors();


    // Find active books with authors and tags loaded
    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("SELECT DISTINCT b FROM Book b WHERE b.archived = false")
    List<Book> findActiveBooksWithDetails();


    // Task 3.4: Find all books without fetching their authors in advance
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithoutFetch();


    // Find books by author name using JPQL
    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("""
           SELECT DISTINCT b
           FROM Book b
           JOIN b.author a
           WHERE LOWER(a.name) = LOWER(:authorName)
           """)
    List<Book> findBooksByAuthorName(
            @Param("authorName") String authorName
    );


    // Find active books by genre
    @Query("""
           SELECT b
           FROM Book b
           WHERE b.genre = :genre
             AND b.archived = false
           """)
    List<Book> findActiveBooksByGenre(
            @Param("genre") Genre genre
    );


    // Find books by genre and year range
    @Query("""
           SELECT b
           FROM Book b
           WHERE b.genre = :genre
             AND b.year BETWEEN :startYear AND :endYear
           ORDER BY b.year DESC
           """)
    List<Book> findByGenreAndYearRange(
            @Param("genre") Genre genre,
            @Param("startYear") int startYear,
            @Param("endYear") int endYear
    );


    // Archive a book by ID
    @Modifying(clearAutomatically = true)
    @Query("""
           UPDATE Book b
           SET b.archived = true
           WHERE b.id = :id
           """)
    int archiveBook(
            @Param("id") Long id
    );


    // Archive all books published in a specific year
    @Modifying(clearAutomatically = true)
    @Query("""
           UPDATE Book b
           SET b.archived = true
           WHERE b.year = :year
           """)
    int archiveBooksByYear(
            @Param("year") int year
    );


    // Task 4: Pagination and sorting
    // Use a simple query so Spring Data can apply sorting and pagination
    @Query(
            value = "SELECT b FROM Book b",
            countQuery = "SELECT COUNT(b) FROM Book b"
    )
    Page<Book> findAllWithDetails(Pageable pageable);

}