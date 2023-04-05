package com.example.demo.controller;

import com.example.demo.dto.BookModelAssembler;
import com.example.demo.persistence.dao.BookRepository;
import com.example.demo.persistence.model.Book;
import com.example.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;


@WebMvcTest
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private BookModelAssembler bookModelAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book1, book2;

    @BeforeEach
    public void setUp() {
        book1 = Book.builder()
                .title("Don Quijote de la Mancha")
                .author("Miguel de Cervantes")
                .price(new BigDecimal("999.95"))
                .releaseDate(LocalDate.now())
                .build();
        book2 = Book.builder()
                .title("Rebelión en la granja")
                .author("George Orwell")
                .price(new BigDecimal("999.95"))
                .releaseDate(LocalDate.now())
                .build();
    }

    @Test
    public void givenBookObject_whenCreateBook_thenReturnSavedBook() throws Exception {

        // given - precondition or setup
        given( bookRepository.save(any(Book.class)) )
                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString( book1 )));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isCreated());
//                .andExpect(jsonPath("$.title", is(book.getTitle())));
//                .andExpect(jsonPath("$.author", is(book.getAuthor())))
//                .andExpect(jsonPath("$.price", is(book.getPrice())))
//                .andExpect(jsonPath("$.releaseDate", is(book.getReleaseDate())));

    }

    // JUnit test for Get All books
    @Test
    public void givenListOfBooks_whenGetAllBooks_thenReturnBookList() throws Exception{
        // given - precondition or setup
        List<Book> bookList = Arrays.asList(book1, book2);
        CollectionModel<EntityModel<Book>> bookCollectionModel = bookModelAssembler.toCollectionModel(bookList);

        given(bookModelAssembler.toCollectionModel(bookService.getBooks())).willReturn(bookCollectionModel);

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/books"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
//                .andExpect(jsonPath("$._embedded.bookList.size()",
//                        is(bookList.size())));
    }

    // positive scenario - valid book id
    // JUnit test for GET book by id
    @Test
    public void givenBookId_whenGetBookById_thenReturnBookObject() throws Exception{
        // given - precondition or setup
        long bookId = 1L;
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book1));
        given( bookService.getBookById(anyLong()) ).willReturn( Optional.of(book1) );

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/books/{id}", bookId));

        // then - verify the output|
        response.andExpect(status().isOk())
                .andDo(print());
//                .andExpect(jsonPath("$.title", is(book.getTitle())))
//                .andExpect(jsonPath("$.author", is(book.getAuthor())))
//                .andExpect(jsonPath("$.price", is(book.getPrice())))
//                .andExpect(jsonPath("$.releaseDate", is(book.getReleaseDate())));

    }

    // negative scenario - valid book id
    // JUnit test for GET book by id
    @Test
    public void givenInvalidBookId_whenGetBookById_thenReturnEmpty() throws Exception{
        // given - precondition or setup
        long bookId = 1L;
        given(bookService.getBookById(bookId)).willReturn(Optional.empty());

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/books/{id}", bookId));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());

    }

    // JUnit test for update book - positive scenario
    @Test
    public void givenUpdatedBook_whenUpdateBook_thenReturnUpdateBookObject() throws Exception{
        // given - precondition or setup
        long bookId = 1L;
        given(bookService.getBookById(bookId)).willReturn(Optional.of(book1));
        given(bookService.updateBook(any(Book.class)))
                .willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/books/{id}", bookId)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(book2)));


        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
//                .andExpect(jsonPath("$.title", is(updatedBook.getTitle())))
//                .andExpect(jsonPath("$.author", is(updatedBook.getAuthor())))
//                .andExpect(jsonPath("$.price", is(updatedBook.getPrice())))
//                .andExpect(jsonPath("$.releaseDate", is(updatedBook.getReleaseDate())));
    }

    // JUnit test for update book - negative scenario
    @Test
    public void givenUpdatedBook_whenUpdateBook_thenReturn404() throws Exception{
        // given - precondition or setup
        long bookId = 1L;
        given(bookService.getBookById(bookId)).willReturn(Optional.empty());
        given(bookService.updateBook(any(Book.class)))
                .willAnswer((invocation)-> invocation.getArgument(0));

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/books/{id}", bookId));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    // JUnit test for delete book
    @Test
    public void givenBookId_whenDeleteBook_thenReturn200() throws Exception{
        // given - precondition or setup
        long bookId = 1L;
        willDoNothing().given(bookService).deleteBookById(bookId);

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/api/books/{id}", bookId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }


}
