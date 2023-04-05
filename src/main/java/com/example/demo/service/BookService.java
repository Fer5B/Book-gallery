package com.example.demo.service;

import com.example.demo.dto.BookSort;
import com.example.demo.dto.PageData;
import com.example.demo.persistence.model.Book;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    List<Book> getBooks();
    Page<Book> getPaginatedBooks(int page);
    Page<Book> getFilteredAndPaginatedBooks(int page, int size, String title, String author, BigDecimal startPrice, BigDecimal endPrice,
                                            LocalDate releaseDateFrom, LocalDate releaseDateTo, String sortBy);
    Optional<Book> getBookById(Long id);
    Book updateBook(Book book);
    void deleteBookById(Long id);
    boolean isExist(Book book);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    Optional<Book> findByTitleAndAuthorAndReleaseDate(String title, String author, LocalDate releaseDate);
    List<Book> findByReleaseDateBetween(LocalDate releaseDateFrom, LocalDate releaseDateTo);
}
