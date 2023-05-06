package com.example.demo.persistence.dao;

import com.example.demo.persistence.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    Optional<Book> findByTitleAndAuthorAndReleaseDate(String title, String author, LocalDate releaseDate);
    Page<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPriceBetweenAndReleaseDateBetween(
            String title, String author, BigDecimal startPrice, BigDecimal endPrice,
            LocalDate releaseDateFrom, LocalDate releaseDateTo, Pageable pageable);

    List<Book> findByReleaseDateBetween(LocalDate releaseDateFrom, LocalDate releaseDateTo);
}
