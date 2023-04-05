package com.example.demo.dto;

import com.example.demo.controller.BookController;
import com.example.demo.persistence.model.Book;
import com.example.demo.service.BookServiceImpl;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {
    @Override
    public EntityModel<Book> toModel(Book book) {
        return EntityModel.of(book,
                linkTo( methodOn(BookController.class).getBookById(book.getId())).withSelfRel(),
//                0, BookServiceImpl.getPaginationValue(),
                linkTo( methodOn(BookController.class).getAllBooks(0, BookServiceImpl.getPaginationValue(),"","","","", LocalDate.MIN, LocalDate.MAX, "")).withRel("books"));
    }

    @Override
    public CollectionModel<EntityModel<Book>> toCollectionModel(Iterable<? extends Book> books) {
        CollectionModel<EntityModel<Book>> entityModelBooks = RepresentationModelAssembler.super.toCollectionModel(books);
        entityModelBooks.add(linkTo(methodOn(BookController.class).getAllBooks(0, BookServiceImpl.getPaginationValue(),"","","","", LocalDate.MIN, LocalDate.MAX, "")).withSelfRel());
        return entityModelBooks;
    }
}
