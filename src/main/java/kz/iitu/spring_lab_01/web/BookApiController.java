package kz.iitu.spring_lab_01.web;

import jakarta.validation.Valid;
import kz.iitu.spring_lab_01.Book;
import kz.iitu.spring_lab_01.dto.BookMapper;
import kz.iitu.spring_lab_01.dto.BookRequest;
import kz.iitu.spring_lab_01.dto.BookResponse;
import kz.iitu.spring_lab_01.service.BookService;
import kz.iitu.spring_lab_01.service.BookNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService service;
    private final BookMapper mapper;

    public BookApiController(BookService service, BookMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<BookResponse> list(
            @RequestParam(required = false) String author) {

        return mapper.toResponseList(service.findAll(author));
    }

    @GetMapping("/{id}")
    public BookResponse find(@PathVariable long id) {

        Book book = service.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        return mapper.toResponse(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody BookRequest request) {

        Book saved = service.create(mapper.toDomain(request));

        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable long id,
            @Valid @RequestBody BookRequest request) {

        Book updated = service.update(id, mapper.toDomain(request));

        return mapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}