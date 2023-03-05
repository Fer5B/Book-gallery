package com.example.demo.service;


import com.example.demo.persistence.model.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    List<Book> getBooks();
    List<Book> getPaginatedBooks(int page);
    Optional<Book> getBookById(Long id);
    Book updateBook(Book book);
    void deleteBookById(Long id);
    boolean isExist(Book book);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    Optional<Book> findByTitleAndAuthorAndReleaseDate(String title, String author, String releaseDate);
    List<Book> findByPriceBetween(BigDecimal startPrice, BigDecimal endPrice);
}
