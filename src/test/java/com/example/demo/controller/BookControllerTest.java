package com.example.demo.controller;

import com.example.demo.dto.BookModelAssembler;
import com.example.demo.error.BookNotFoundException;
import com.example.demo.error.ErrorAdvice;
import com.example.demo.persistence.model.Book;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

//@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {BookController.class, BookModelAssembler.class, ErrorAdvice.class})
public class BookControllerTest {

    @MockBean
    BookService bookService;

    @Autowired
    BookModelAssembler bookModelAssembler;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book1, book2;

    @BeforeEach
    public void setUp() {
        book1 = Book.builder()
                .id(1L)
                .title("Don Quijote de la Mancha")
                .author("Miguel de Cervantes")
                .price(new BigDecimal("999.95"))
                .releaseDate(LocalDate.now())
                .build();
        book2 = Book.builder()
                .id(2L)
                .title("Rebelión en la granja")
                .author("George Orwell")
                .price(new BigDecimal("999.95"))
                .releaseDate(LocalDate.now())
                .build();
    }

    // JUnit test for POST new book
    @Test
    public void givenBookObject_whenCreateBook_thenReturnSavedBook() throws Exception {
//        given - precondition or setup
        Mockito.when(bookService.saveBook( any(Book.class) )).thenReturn(book1);

//        when - action
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(book1)))

//        then - verify the output
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(book1.getTitle())))
                .andExpect(jsonPath("$.author", is(book1.getAuthor())))
                .andExpect(jsonPath("$.price", is(book1.getPrice().doubleValue())))
                .andExpect(jsonPath("$.releaseDate", is(book1.getReleaseDate().toString())))
                .andDo(print());

    }

    // JUnit test for GET all books
    @Test
    public void givenBookList_whenGetAllBooks_thenReturnBookList() throws Exception {
//       given
        List<Book> bookList = Arrays.asList(book1, book2);
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").descending());
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        Mockito.when(bookService.getFilteredAndPaginatedBooks(
                anyInt(), anyInt(), anyString(), anyString(), any(BigDecimal.class), any(BigDecimal.class),
                any(LocalDate.class), any(LocalDate.class), anyString()
        )).thenReturn(bookPage);

//        when
         mockMvc.perform(get("/api/books"))
//                 then
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$._embedded.bookList.size()", is(bookList.size())))
                 .andExpect(jsonPath("$._embedded.bookList[0].title", is(bookList.get(0).getTitle())))
                 .andDo(print());
    }

    // JUnit test for GET book by id
    // positive scenario - valid book id
    @Test
    public void givenBookId_whenGetBookById_thenReturnEntityModelOfBook() throws Exception {
        Long id = 1L;
        Mockito.when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book1));

        mockMvc.perform(get("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(book1.getTitle())))
                .andExpect(jsonPath("$.author", is(book1.getAuthor())))
                .andExpect(jsonPath("$.price", is(book1.getPrice().doubleValue())))
                .andExpect(jsonPath("$.releaseDate", is(book1.getReleaseDate().toString())))
                .andDo(print());
    }

    // JUnit test for GET book by id
    // negative scenario - invalid book id
    @Test
    public void givenInvalidBookId_whenGetBookById_thenReturnBookNotFoundException() throws Exception {
        Long bookId = 1L;
        BookNotFoundException bookNotFoundException = new BookNotFoundException(bookId);

        Mockito.when(bookService.getBookById(anyLong())).thenThrow(bookNotFoundException);

        ResultActions response = mockMvc.perform(get("/api/books/{id}", bookId));

        response
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookNotFoundException))
                .andExpect(result -> assertSame(bookNotFoundException, result.getResolvedException() ))
                .andExpect(result -> assertEquals(bookNotFoundException.getMessage(), result.getResolvedException().getMessage() ))
                .andDo(print());
    }

    // JUnit test for PUT book update
    // positive scenario - valid book id and json object in request body
    @Test
    public void givenBookObjectWithId_whenUpdateBook_thenReturnUpdatedBookObject() throws Exception {
        Long bookId = 1L;
        Book updatedBook = Book.builder().id(bookId).title("new title").author("new author")
                .price(new BigDecimal("150998.10")).releaseDate(LocalDate.now()).build();
        Mockito.when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book1));
        Mockito.when(bookService.updateBook(any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(updatedBook.getTitle())))
                .andExpect(jsonPath("$.author", is(updatedBook.getAuthor())))
                .andExpect(jsonPath("$.price", is(updatedBook.getPrice().doubleValue())))
                .andExpect(jsonPath("$.releaseDate", is(updatedBook.getReleaseDate().toString())))
                .andExpect(result -> {
                    assertEquals(book1.getTitle(), objectMapper.readValue(result.getResponse().getContentAsString(), Book.class).getTitle());
                    assertEquals(book1.getAuthor(), objectMapper.readValue(result.getResponse().getContentAsString(), Book.class).getAuthor());
                    assertEquals(book1.getPrice(), objectMapper.readValue(result.getResponse().getContentAsString(), Book.class).getPrice());
                    assertEquals(book1.getReleaseDate(), objectMapper.readValue(result.getResponse().getContentAsString(), Book.class).getReleaseDate());
                })
                .andDo(print());
    }

    // JUnit test for PUT book update
    // negative scenario - valid book id but invalid json object in request body
    @Test
    public void givenInvalidJsonObject_whenUpdateBook_thenReturnHttpMessageNotReadableException() throws Exception {
        Mockito.when( bookService.getBookById(anyLong()) ).thenReturn(Optional.of(book1));
        String invalidJson = "This doesn't work as a valid json";

        assertThrows(HttpMessageNotReadableException.class, () -> {
            throw mockMvc.perform(put("/api/books/{id}", book1.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof  HttpMessageNotReadableException))
                    .andDo(print())
                    .andReturn().getResolvedException();
        });
    }
    // JUnit test for PUT book update
    // negative scenario - valid book id and valid json object in request body but invalid property values
    @Test
    public void givenInvalidValuePropertyInJsonObject_whenUpdateBook_thenReturnMethodArgumentNotValidException() throws Exception {
        Mockito.when( bookService.getBookById(anyLong()) ).thenReturn(Optional.of(book1));
        String invalidJsonPropertyValues = objectMapper.writeValueAsString(Book.builder().title("   ").author("").build());
        String[] properties = {"title", "author", "price", "release date"};
        Function<String, String> errorMessage = (s) -> s + " is mandatory";

        assertThrows(MethodArgumentNotValidException.class, () -> {
            throw mockMvc.perform(put("/api/books/{id}", book1.getId())
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(invalidJsonPropertyValues))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof  MethodArgumentNotValidException))
                    .andExpect(result -> {
                        String response = result.getResponse().getContentAsString().toLowerCase();
                        Arrays.stream(properties).forEach(property -> assertTrue(response.contains(errorMessage.apply(property))));
                    })
                    .andDo(print())
                    .andReturn().getResolvedException();
        });
    }

    // JUnit test for PATCH the price of the book
    // positive scenario - valid book id and price text string
    @Test
    public void givenPriceInString_whenUpdatePriceBook_thenReturnUpdatedBookObject() throws Exception {
        Long bookId = 1L;
        String oldPrice = book1.getPrice().toPlainString();
        String newPrice = "10000";
        Mockito.when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book1));
        Mockito.when(bookService.updateBook(any(Book.class))).thenReturn(book1);

        mockMvc.perform(patch("/api/books/{id}", bookId)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content(newPrice))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(),
                        Book.class).getPrice().toPlainString(), newPrice))
                .andExpect(result -> assertNotEquals(book1.getPrice().toPlainString(), oldPrice))
                .andDo(print());
    }
    // JUnit test for PATCH the price of the book
    // negative scenario - valid book id but invalid price in string
    @Test
    public void givenInvalidPriceInString_whenUpdatePriceBook_thenReturnBadRequest() throws Exception {
        Long bookId = 1L;
        String newPrice = "bad price";
        String errorMessage = "It is mandatory to denote a digit that represents the new price of the book";

        Mockito.when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book1));

        mockMvc.perform(patch("/api/books/{id}", bookId)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .content(newPrice))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(errorMessage, result.getResponse().getContentAsString()))
                .andDo(print());
    }

    @Test
    public void givenBookId_whenDeleteBook_thenReturnDeletedBookObject() throws Exception {
        Mockito.when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book1));
        willDoNothing().given(bookService).deleteBookById(anyLong());

        mockMvc.perform(delete("/api/books/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(book1.getId().intValue())))
                .andExpect(jsonPath("$.title", is(book1.getTitle())))
                .andExpect(result -> assertEquals(book1, objectMapper.readValue(result.getResponse().getContentAsString(), Book.class)))
                .andDo(print());
    }

}
