package kz.iitu.spring_lab_01.dto;

import kz.iitu.spring_lab_01.domain.Genre;
import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        int year,
        Genre genre,
        Instant createdAt
) {
}