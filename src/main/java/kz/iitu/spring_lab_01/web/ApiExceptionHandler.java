package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.service.BookNotFoundException;
import kz.iitu.spring_lab_01.service.DuplicateTitleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail handleNotFound(BookNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setType(
                URI.create("https://api.iitu.kz/errors/book-not-found")
        );
        problem.setTitle("Book not found");
        return problem;
    }
    @ExceptionHandler(DuplicateTitleException.class)
    public ProblemDetail handleDuplicate(DuplicateTitleException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problem.setTitle("Duplicate title");
        problem.setProperty("title", ex.getTitle());
        return problem;
    }

}