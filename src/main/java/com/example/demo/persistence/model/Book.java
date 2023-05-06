package com.example.demo.persistence.model;

import com.example.demo.validation.MyDateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank(message = "Title is mandatory")
    private String title;
    @NotNull
    @NotBlank(message = "Author is mandatory")
    private String author;
    @NotNull(message = "Price is mandatory")
    private BigDecimal price;
    @NotNull(message = "Release date is mandatory")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//    @Schema(type = "date", pattern = "dd-MM-yyyy", example = "01-01-1000")
//    @MyDateTimeFormat
    private LocalDate releaseDate;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime lastModify;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(releaseDate, book.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, releaseDate);
    }
}
