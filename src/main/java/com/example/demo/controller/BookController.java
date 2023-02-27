package com.example.demo.controller;

import com.example.demo.dto.BookModelAssembler;
import com.example.demo.error.BookNotFoundException;
import com.example.demo.persistence.model.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookModelAssembler bookModelAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Book> createBook(@RequestBody Book book) {

        EntityModel<Book> bookEntityModel = bookModelAssembler.toModel(bookService.saveBook(book));

        return bookEntityModel;

//        return ResponseEntity
//                .created(bookEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
//                .body(bookEntityModel);
    }

    @GetMapping
    public CollectionModel<EntityModel<Book>> getAllBooks() {
        return bookModelAssembler.toCollectionModel(bookService.getBooks());
    }

    @GetMapping("/{id}")
    public EntityModel<Book> getBookById(@PathVariable long id) {
        return bookService.getBookById(id)
                .map(book -> bookModelAssembler.toModel(book))
                .orElseThrow(() -> new BookNotFoundException(id) );
    }



    @PutMapping("/{id}")
    public EntityModel<Book> updateBook(@RequestBody Book book, @PathVariable long id) {
        return bookService.getBookById(id)
                .map(b -> {
                    b.setAuthor(book.getAuthor());
                    b.setPrice(book.getPrice());
                    b.setTitle(book.getTitle());
                    b.setReleaseDate(book.getReleaseDate());
                    return bookModelAssembler.toModel(bookService.updateBook(b));
                })
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable long id) {
        return bookService.getBookById(id)
                .map(b -> {bookService.deleteBookById(b.getId()); return ResponseEntity.ok(bookModelAssembler.toModel(b)); } )
                .orElseThrow(() -> new BookNotFoundException(id));
    }

}
