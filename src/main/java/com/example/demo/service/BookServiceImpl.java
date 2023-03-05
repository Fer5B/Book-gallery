package com.example.demo.service;

import com.example.demo.persistence.dao.BookRepository;
import com.example.demo.persistence.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final int PAGINATION_VALUE = 10;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getPaginatedBooks(int page) {
        return bookRepository.findAll(PageRequest.of(page, PAGINATION_VALUE)).toList();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public boolean isExist(Book book) {
        return findByTitleAndAuthorAndReleaseDate(book.getTitle(), book.getAuthor(), book.getReleaseDate()).isPresent();
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public Optional<Book> findByTitleAndAuthorAndReleaseDate(String title, String author, String releaseDate) {
        return bookRepository.findByTitleAndAuthorAndReleaseDate(title, author, releaseDate);
    }

    @Override
    public List<Book> findByPriceBetween(BigDecimal startPrice, BigDecimal endPrice) {
        return bookRepository.findByPriceBetween(startPrice, endPrice);
    }

}
