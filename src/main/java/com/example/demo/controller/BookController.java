package com.example.demo.controller;

import com.example.demo.dto.BookModelAssembler;
import com.example.demo.error.BookAlreadyExistException;
import com.example.demo.error.BookNotFoundException;
import com.example.demo.persistence.model.Book;
import com.example.demo.service.BookService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookModelAssembler bookModelAssembler;

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "create-book", summary = "Agregar un libro")
    @ApiResponse(responseCode = "201", description = "Se retorna el libro recién creado y agregado al sistema.")
    @Parameter(name = "book", description = "Objeto JSON representando a las propiedades de la entidad Book.",
            required = false)
    public ResponseEntity<?>  createBook(@Valid @RequestBody Book book) {

        if(bookService.isExist(book)) {
            throw new BookAlreadyExistException(book);
        }

        EntityModel<Book> bookEntityModel = bookModelAssembler.toModel(bookService.saveBook(book));

        return ResponseEntity
                .created(bookEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(bookEntityModel);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "get-books", summary = "Obtener una lista de libros")
    @ApiResponse(responseCode = "200", description = "Se retorna una lista de libros o una lista vacía de no haber " +
            "libros en la base de datos. Se define la represención de las relaciones de enlaces en cada tipo de datos devuelto.")
    @Parameter(name = "page", description = "Indica el número de página de la que se quiere obtener los libros.",
            required = true)
    public CollectionModel<EntityModel<Book>> getAllBooks(@RequestParam(required = false, defaultValue = "0") int page) {
        return bookModelAssembler.toCollectionModel(bookService.getPaginatedBooks(page));
    }

    @GetMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "get-book", summary = "Obtener un libro")
    @ApiResponse(responseCode = "200", description = "Se retorna el libro con el identificador buscado.")
    @Parameter(name = "id", description = "Identificador único del libro", required = true)
    public EntityModel<Book> getBookById(@PathVariable long id) {
        return bookService.getBookById(id)
                .map(book -> bookModelAssembler.toModel(book))
                .orElseThrow(() -> new BookNotFoundException(id) );
    }

    @PutMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "update-book", summary = "Actualizar un libro")
    @ApiResponse(responseCode = "200", description = "Se retorna el libro actualizado.")
    @Parameters({
            @Parameter(name = "book", description = "Objeto JSON representando a las propiedades de la entidad Book.", required = true),
            @Parameter(name = "id", description = "Identificador único del libro.", required = true)
    })
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

    @PatchMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "patch-book", summary = "Actualizar el precio de un libro")
    @ApiResponse(responseCode = "200", description = "Se retorna el libro actualizado.")
    @Parameters({
            @Parameter(name = "price", description = "Cadena de texto que represente el valor que tendrá el nuevo precio del libro.", required = true),
            @Parameter(name = "id", description = "Identificador único del libro.", required = true)
    })
    public ResponseEntity<?> updateBookPrice(@RequestBody String price, @PathVariable long id) {
        return bookService.getBookById(id)
                .map(b -> {
                    try {
                        b.setPrice(new BigDecimal(price));
                    }
                    catch (Exception e) {
                        return ResponseEntity.badRequest().body("It is mandatory to denote a digit that represents the new price of the book");
                    }
                    return ResponseEntity.ok(bookModelAssembler.toModel(bookService.updateBook(b)));
                })
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @DeleteMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "delete-book", summary = "Eliminar un libro")
    @ApiResponse(responseCode = "200", description = "Se retorna el libro eliminado.")
    @Parameter(name = "id", description = "Identificador único del libro.", required = true)
    public ResponseEntity<?> deleteBook(@PathVariable long id) {
        return bookService.getBookById(id)
                .map(b -> {bookService.deleteBookById(b.getId()); return ResponseEntity.ok(bookModelAssembler.toModel(b)); } )
                .orElseThrow(() -> new BookNotFoundException(id));
    }

}
