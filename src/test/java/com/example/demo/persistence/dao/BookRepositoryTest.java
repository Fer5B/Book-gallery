package com.example.demo.persistence.dao;

import com.example.demo.persistence.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void findByTitle() {
        String title = "Same title";
        bookRepository.save(Book.builder().title(title).author("author1").price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        bookRepository.save(Book.builder().title(title).author("author2").price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        bookRepository.save(Book.builder().title("other title").author("author3").price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        List<Book> bookList = bookRepository.findByTitle(title);

        assertEquals(2, bookList.size());
        bookList.forEach(book -> assertSame(title, book.getTitle()));
    }

    @Test
    void findByAuthor() {
        String author = "Same author";
        bookRepository.save(Book.builder().title("title1").author(author).price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        bookRepository.save(Book.builder().title("title2").author(author).price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        bookRepository.save(Book.builder().title("title3").author("other author").price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        List<Book> bookList = bookRepository.findByAuthor(author);

        assertEquals(2, bookList.size());
        bookList.forEach(book -> assertSame(author, book.getAuthor()));
    }

    @Test
    void findByTitleAndAuthorAndReleaseDate() {
        String title = "Same title", author = "Same author";
        LocalDate releaseDate = LocalDate.EPOCH;
        Book bookMatch = Book.builder().title(title).author(author).price(BigDecimal.ONE).releaseDate(releaseDate).build();

        bookRepository.save(bookMatch);
        bookRepository.save(Book.builder().title(title).author(author).price(BigDecimal.ONE).releaseDate(LocalDate.now()).build());
        bookRepository.save(Book.builder().title(title).author("other author").price(BigDecimal.ONE).releaseDate(releaseDate).build());
        bookRepository.save(Book.builder().title("other title").author(author).price(BigDecimal.ONE).releaseDate(releaseDate).build());

        Optional<Book> optionalBook = bookRepository.findByTitleAndAuthorAndReleaseDate(title, author, releaseDate);

        assertTrue(optionalBook.isPresent());
        assertTrue(bookMatch.equals(optionalBook.get()));
        assertSame(title, optionalBook.get().getTitle());
        assertSame(author, optionalBook.get().getAuthor());
        assertSame(releaseDate, optionalBook.get().getReleaseDate());
    }

    @Test
    void findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPriceBetweenAndReleaseDateBetween() {
        String title = "Same title", author = "Same author";
        LocalDate releaseDateFrom = LocalDate.of(1990, 10, 10),
        releaseDateTo = LocalDate.of(2020, 01, 20);
        BigDecimal startPrice = new BigDecimal("10000"), endPrice = new BigDecimal("50000");

        Book bookMatch1 = Book.builder().title(title).author(author).price(new BigDecimal("35000")).releaseDate(LocalDate.of(2000, 10, 30)).build();
        Book bookMatch2 = Book.builder().title(title + " VII").author(author).price(new BigDecimal("20000")).releaseDate(LocalDate.of(2005, 10, 30)).build();
        Book bookMatch3 = Book.builder().title(title).author(author + " and ?").price(new BigDecimal("48000")).releaseDate(LocalDate.of(2010, 10, 30)).build();

        bookRepository.save(bookMatch1);
        bookRepository.save(bookMatch2);
        bookRepository.save(bookMatch3);
        bookRepository.save(Book.builder().title(title).author(author).price(new BigDecimal("15000")).releaseDate(LocalDate.of(2021, 02, 12)).build());
        bookRepository.save(Book.builder().title(title).author("other author").price(new BigDecimal("40000")).releaseDate(releaseDateFrom).build());
        bookRepository.save(Book.builder().title("other title").author(author).price(new BigDecimal("25000")).releaseDate(releaseDateTo).build());
        bookRepository.save(Book.builder().title(title).author(author).price(new BigDecimal("5000")).releaseDate(LocalDate.of(2010, 05, 25)).build());

        Page<Book> pageBook = bookRepository
                .findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPriceBetweenAndReleaseDateBetween(
                        title, author, startPrice, endPrice, releaseDateFrom, releaseDateTo, PageRequest.of(0, 2, Sort.by("title")));

        assertTrue(pageBook.hasContent());
        assertTrue(pageBook.toList().contains(bookMatch1));
        assertTrue(pageBook.toList().contains(bookMatch2));
        assertTrue(pageBook.toList().contains(bookMatch3));
        assertEquals(2, pageBook.getTotalPages());
        assertEquals(3, pageBook.getTotalElements());
    }

    @Test
    void findByReleaseDateBetween() {
        String title = "title", author = "author";
        LocalDate releaseDateFrom = LocalDate.of(2010, 10, 10),
                releaseDateTo = LocalDate.now();

        Book bookMatch1 = Book.builder().title(title).author(author).price(BigDecimal.ONE).releaseDate(LocalDate.of(2015, 12, 20)).build();
        Book bookMatch2 = Book.builder().title(title).author(author).price(BigDecimal.ONE).releaseDate(releaseDateTo).build();

        bookRepository.save(bookMatch1);
        bookRepository.save(bookMatch2);
        bookRepository.save(Book.builder().title(title).author(author).price(BigDecimal.ONE).releaseDate(LocalDate.EPOCH).build());

        List<Book> bookList = bookRepository.findByReleaseDateBetween(releaseDateFrom, releaseDateTo);

        assertEquals(2, bookList.size());
        assertTrue(bookList.contains(bookMatch1));
        assertTrue(bookList.contains(bookMatch2));
        bookList.forEach(book -> assertTrue(
                book.getReleaseDate().isEqual(releaseDateFrom) ||
                        book.getReleaseDate().isAfter(releaseDateFrom) &&
                        book.getReleaseDate().isEqual(releaseDateTo) ||
                        book.getReleaseDate().isBefore(releaseDateTo)
        ));
    }
}