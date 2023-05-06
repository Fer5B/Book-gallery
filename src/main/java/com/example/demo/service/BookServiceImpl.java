package com.example.demo.service;

import com.example.demo.persistence.dao.BookRepository;
import com.example.demo.persistence.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class BookServiceImpl implements BookService {

    private static final int PAGINATION_VALUE = 10;

    @Autowired
    private BookRepository bookRepository;

    public static int getPaginationValue() {
        return PAGINATION_VALUE;
    }

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> getPaginatedBooks(int page) {
        return bookRepository.findAll(PageRequest.of(page, PAGINATION_VALUE));
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book saveBook(Book book) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        book.setCreatedAt(zonedDateTime);
        book.setLastModify(zonedDateTime);
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        book.setLastModify(ZonedDateTime.now(ZoneId.systemDefault()));
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
    public Optional<Book> findByTitleAndAuthorAndReleaseDate(String title, String author, LocalDate releaseDate) {
        return bookRepository.findByTitleAndAuthorAndReleaseDate(title, author, releaseDate);
    }

    @Override
    public Page<Book> getFilteredAndPaginatedBooks(int page, int size, String title, String author, BigDecimal startPrice, BigDecimal endPrice,
                                                   LocalDate releaseDateFrom, LocalDate releaseDateTo, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(getOrderListFromString(sortBy)));

        return bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPriceBetweenAndReleaseDateBetween(
                title, author, startPrice, endPrice, releaseDateFrom, releaseDateTo, pageable);
    }

    @Override
    public List<Book> findByReleaseDateBetween(LocalDate releaseDateFrom, LocalDate releaseDateTo) {
        return bookRepository.findByReleaseDateBetween(releaseDateFrom, releaseDateTo);
    }

    private List<Sort.Order> getOrderListFromString(String sortBy) {
        Set<Sort.Order> orders = new HashSet<>();

        try {
            String[] bookSortData = sortBy.replaceAll(" ","").split(",");

            for (String fieldSortData: bookSortData) {
                String[] fieldSortDirection = fieldSortData.split(":");
                orders.add(new Sort.Order(Sort.Direction.valueOf(fieldSortDirection[1].toUpperCase()), fieldSortDirection[0]));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return orders.stream().toList();
    }


}
