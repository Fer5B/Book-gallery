package com.example.demo.service;

import com.example.demo.persistence.dao.BookRepository;
import com.example.demo.persistence.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Autowired
    private BookService bookService = new BookServiceImpl();

    @MockBean
    private BookRepository bookRepository;

    private List<Book> bookList;
    private LocalDate localDate;

    @BeforeEach
    void setUp() throws Exception {
        localDate = LocalDate.now();
        bookList = new ArrayList<>( Arrays.asList(
                Book.builder().id(1L).title("B1").author("A1").releaseDate(localDate).price(BigDecimal.valueOf(1000)).build(),
                Book.builder().id(2L).title("B2").author("A2").releaseDate(localDate).price(BigDecimal.valueOf(2000)).build(),
                Book.builder().id(3L).title("B3").author("A3").releaseDate(localDate).price(BigDecimal.valueOf(3000)).build(),
                Book.builder().id(4L).title("B4").author("A4").releaseDate(localDate).price(BigDecimal.valueOf(4000)).build()
        ));
        when(bookRepository.findAll()).thenReturn(bookList);
    }

    @Test
    void testGetBooks() {
        List<Book> books = bookService.getBooks();

        assertEquals(4, books.size());
        verify(bookRepository, only()).findAll();
    }

    @Test
    void testGetPaginatedBooks() {
        PageImpl<Book> page = new PageImpl<>(bookList, PageRequest.of(0, 2), bookList.size());
        when(bookRepository.findAll( any(Pageable.class) )).thenReturn( page );
        Page<Book> bookPage = bookService.getPaginatedBooks(0);

        assertTrue(bookPage.hasContent());
        assertEquals(2, bookPage.getTotalPages());
        assertEquals(4, bookPage.getTotalElements());
        assertSame(bookList.get(0), bookPage.getContent().get(0));
        assertNotSame(bookList.get(1), bookPage.toList().get(2));
    }

    @Test
    void testGetBookById() {
        long id = 1L;
        when(bookRepository.findById(id)).thenReturn( Optional.of(bookList.get(0)) );
        Optional<Book> optionalBook = bookService.getBookById(id);

        assertTrue(optionalBook.isPresent());
        verify(bookRepository).findById( anyLong() );
    }

    @Test
    void testSaveBook() {
        long id = Long.valueOf(bookList.size()) + 1;
        Book book = Book.builder().id(id).title("B"+id).author("A"+id).price(BigDecimal.valueOf(id * 1000))
                .releaseDate(localDate).build();
        when(bookRepository.save( any(Book.class) )).thenAnswer( i -> {bookList.add(book); return book; } );
        Book savedBook = bookService.saveBook(book);

        assertEquals(id, bookList.size());
        assertEquals(book, savedBook);
        assertTrue(Objects.equals(book.getTitle(), savedBook.getTitle()) );
        verify(bookRepository).save( any(Book.class) );
    }

    @Test
    void testUpdateBook() {
        int id = bookList.size();
        Book book = Book.builder().id(Long.valueOf(id)).title("update-B"+id).author("update-A"+id).price(BigDecimal.valueOf(id * 1000 + 1))
                .releaseDate(localDate).build();
        when(bookRepository.save( any(Book.class) )).thenAnswer( i -> {bookList.set(id-1, book); return bookList.get(id-1); });
        Book updatedBook = bookService.updateBook(book);

        assertEquals(book.getTitle(), bookList.get(id-1).getTitle());
        assertEquals(book.hashCode(), updatedBook.hashCode());
        assertTrue(Objects.equals(book.getAuthor(), updatedBook.getAuthor()) );
        assertTrue(book.equals(updatedBook));
        verify(bookRepository).save( any(Book.class) );
    }

    @Test
    void testDeleteBookById() {
        int initSize = bookList.size();
        long id = 2L;
        Book bookToDelete = bookList.get((int)id-1);
        doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            System.out.println("Argument(0): " + args.toString());
            bookList.remove((int)id-1);
            return null;
        }).when(bookRepository).deleteById(anyLong());

        bookService.deleteBookById(id);

        assertEquals(initSize-1, bookList.size());
        assertFalse(bookList.contains(bookToDelete));
    }

    //  positive scenario
    @Test
    void testIsExist() {
        Book book = Book.builder().title("B1").author("A1").releaseDate(localDate).build();
        when(bookRepository.findByTitleAndAuthorAndReleaseDate(anyString(), anyString(), any(LocalDate.class)))
                .thenAnswer(e -> {
                    int size = bookList.size();
                    for (int i = 0; i < size; i++) {
                        if(bookList.get(i).equals(book))
                            return Optional.of(book);
                    }
                    return Optional.empty();
                });

        boolean isExist = bookService.isExist(book);

        assertTrue(isExist);
        verify(bookRepository).findByTitleAndAuthorAndReleaseDate ( anyString(), anyString(), any(LocalDate.class) );
    }
    //  negative scenario
    @Test
    void testIsExist_Negative() {
        Book book = Book.builder().title("B5").author("A3").releaseDate(LocalDate.now()).build();
        when(bookRepository.findByTitleAndAuthorAndReleaseDate(anyString(), anyString(), any(LocalDate.class)))
                .thenAnswer(e -> {
                    int size = bookList.size();
                    for (int i = 0; i < size; i++) {
                        if(bookList.get(i).equals(book))
                            return Optional.of(book);
                    }
                    return Optional.empty();
                });

        boolean isExist = bookService.isExist(book);

        assertFalse(isExist);
        verify(bookRepository).findByTitleAndAuthorAndReleaseDate ( anyString(), anyString(), any(LocalDate.class) );
    }

    @Test
    void testFindByTitle() {
        when(bookRepository.findByTitle( anyString() )).thenReturn(bookList.stream().filter(book -> "B1".equals(book.getTitle())).collect(Collectors.toList()) );
        List<Book> books = bookService.findByTitle("B1");

        assertEquals(1, books.size());
        assertEquals("B1", books.get(0).getTitle());

        verify(bookRepository).findByTitle(anyString());
    }


//    @Test
//    void testGetFilteredAndPaginatedBooks() {
//        int page = 0;
//        String title = "B1", author = "A1";
//        BigDecimal startPrice = new BigDecimal("500"), endPrice = new BigDecimal("1500");
//
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("title").descending());
//
//        when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPriceBetween(
//                anyString(), anyString(), any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)
//        )).thenAnswer(e -> {
//            List<Book> books = bookList.stream().filter(book -> book.getTitle().contains(title) && book.getAuthor().contains(author)
//                    && book.getPrice().doubleValue() >= startPrice.doubleValue() && book.getPrice().doubleValue() <= endPrice.doubleValue()
//            ).collect(Collectors.toList());
//
//            return new PageImpl<>(books, pageable, books.size());
//        });
//
//        Page<Book> books = bookService.getFilteredAndPaginatedBooks(page, title, author, startPrice, endPrice);
//
//        assertTrue(books.hasContent());
//        assertEquals(1, books.getTotalPages());
//        assertEquals(1, books.getTotalElements());
//        assertSame(bookList.get(0), books.getContent().get(0));
//        assertNotSame(bookList.get(1), books.toList().get(0));
//        verify(bookRepository).findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndPriceBetween(
//                anyString(), anyString(), any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)
//        );
//    }

}
