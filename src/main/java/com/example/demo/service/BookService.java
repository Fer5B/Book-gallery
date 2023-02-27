package com.example.demo.service;


import com.example.demo.persistence.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book saveBook(Book book);
    List<Book> getBooks();
    Optional<Book> getBookById(Long id);
    Book updateBook(Book book);
    void deleteBookById(Long id);
}
