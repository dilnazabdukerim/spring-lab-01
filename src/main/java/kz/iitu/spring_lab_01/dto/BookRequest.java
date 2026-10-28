package kz.iitu.spring_lab_01.dto;

import kz.iitu.spring_lab_01.domain.Genre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank
        @Size(max = 120)
        String title,

        @NotBlank
        String author,

        @Min(1450)
        @Max(2100)
        int year,

        @NotNull
        Genre genre
) {
}