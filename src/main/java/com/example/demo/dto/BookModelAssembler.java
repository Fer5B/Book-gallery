package com.example.demo.dto;

import com.example.demo.controller.BookController;
import com.example.demo.persistence.model.Book;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<Book, EntityModel<Book>> {
    @Override
    public EntityModel<Book> toModel(Book book) {
        return EntityModel.of(book,
                linkTo( methodOn(BookController.class).getBookById(book.getId())).withSelfRel(),
                linkTo( methodOn(BookController.class).getAllBooks()).withRel("books"));
    }

    @Override
    public CollectionModel<EntityModel<Book>> toCollectionModel(Iterable<? extends Book> books) {
        CollectionModel<EntityModel<Book>> entityModelBooks = RepresentationModelAssembler.super.toCollectionModel(books);
        entityModelBooks.add(linkTo(methodOn(BookController.class).getAllBooks()).withSelfRel());
        return entityModelBooks;
    }
}
