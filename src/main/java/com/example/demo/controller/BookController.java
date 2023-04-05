package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.error.BookAlreadyExistException;
import com.example.demo.error.BookNotFoundException;
import com.example.demo.persistence.model.Book;
import com.example.demo.service.BookService;
import com.example.demo.validation.BookOrders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookModelAssembler bookModelAssembler;

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "create-book", summary = "Agregar un libro")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto JSON representando a las propiedades de la entidad Book.",
            content = @Content(schema = @Schema(implementation = Book.class )) )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created",
                    content = @Content(schema = @Schema(implementation = Book.class)) ),
            @ApiResponse(responseCode = "409", description = "Book already exist", content = @Content(schema = @Schema(type = "string")))
    })
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
    @Operation(operationId = "get-books", summary = "Obtener una lista de libros", description = "Parámetros de búsqueda")
    @Parameters(value = {
            @Parameter(name = "page", description = "Número de página"),
            @Parameter(name = "size", description = "Tamaño de la página devuelta"),
            @Parameter(name = "title", description = "Título del libro"),
            @Parameter(name = "author", description = "Autor del libro"),
            @Parameter(name = "startPrice", description = "Precio límite inferior"),
            @Parameter(name = "endPrice", description = "Precio límite superior"),
            @Parameter(name = "releaseDateFrom", description = "Fecha de publicación desde. Formato: dd-mm-yyyy", example = "01-01-0001", schema = @Schema(type = "date")),
            @Parameter(name = "releaseDateTo", description = "Fecha de publicación hasta. Formato: dd-mm-yyyy", example = "31-12-9999", schema = @Schema(type = "date")),
            @Parameter(name = "sortBy", description = "Se admite un criterio de ordenación múltiple indicando la propiedad " +
                    "seguido de dos puntos y la dirección de ordenación (ASC: orden ascendente, DESC: orden descendente). Cada propiedad debe estar separada por coma con orden de prioridad de izquierda a derecha. " +
                    "\n\nEj.: 'title:DESC, price:ASC, releaseDate:DESC, author:ASC'")
    })
    @ApiResponse(responseCode = "200", description = "Se retorna una lista de libros o una lista vacía de no haber " +
            "libros en la base de datos. Se define la represención de las relaciones de enlaces en cada tipo de datos devuelto.")
    public ResponseEntity<?> getAllBooks(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, defaultValue = "0") String startPrice,
            @RequestParam(required = false, defaultValue = "1000000") String endPrice,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDate).of(0001,01,01)}")
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate releaseDateFrom,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDate).of(9999,12,31)}")
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate releaseDateTo,
            @RequestParam(required = false, defaultValue = "")
            @BookOrders String sortBy
    )
    {
        CollectionModel<EntityModel<Book>> collectionModel;
        try {
            BigDecimal startPrice_BigDecimal = new BigDecimal(startPrice),
                    endPrice_BigDecimal = new BigDecimal(endPrice);

            Page<Book> bookPage = bookService.getFilteredAndPaginatedBooks(page, size, title, author,
                    startPrice_BigDecimal, endPrice_BigDecimal, releaseDateFrom, releaseDateTo, sortBy);

            collectionModel = bookModelAssembler.toCollectionModel( bookPage );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Enter a valid numeric value to filter by book price.");
        }
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "get-book", summary = "Obtener un libro")
    @Parameter(name = "id", description = "Identificador único del libro", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se retorna el libro con el identificador buscado.",
                    content = @Content(schema = @Schema(implementation = Book.class)) ),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(type = "string")))
    })
    public EntityModel<Book> getBookById(@PathVariable long id) {
        return bookService.getBookById(id)
                .map(book -> bookModelAssembler.toModel(book))
                .orElseThrow(() -> new BookNotFoundException(id) );
    }

    @PutMapping(path = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "update-book", summary = "Actualizar un libro")
    @Parameter(name = "id", description = "Identificador único del libro.", required = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto JSON representando a las propiedades de la entidad Book.",
            content = @Content(schema = @Schema(implementation = Book.class)), required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se retorna el libro actualizado.",
                    content = @Content(schema = @Schema(implementation = Book.class)) ),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(type = "string")))
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
    @Parameter(name = "id", description = "Identificador único del libro.", required = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Cadena de texto que represente el valor que tendrá el nuevo precio del libro.",
            content = @Content(schema = @Schema(type = "string")), required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se retorna el libro actualizado.",
                    content = @Content(schema = @Schema(implementation = Book.class)) ),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(type = "string")))

    })
    public ResponseEntity<?> updateBookPrice(@RequestBody String price, @PathVariable long id) {
        return bookService.getBookById(id)
                .map(b -> {
                    try {
                        b.setPrice(new BigDecimal(price.trim()));
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
    @Parameter(name = "id", description = "Identificador único del libro.", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se retorna el libro eliminado.",
                    content = @Content(schema = @Schema(implementation = Book.class)) ),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "Book already exist", content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<?> deleteBook(@PathVariable long id) {
        return bookService.getBookById(id)
                .map(b -> {bookService.deleteBookById(b.getId()); return ResponseEntity.ok(bookModelAssembler.toModel(b)); } )
                .orElseThrow(() -> new BookNotFoundException(id));
    }

}
