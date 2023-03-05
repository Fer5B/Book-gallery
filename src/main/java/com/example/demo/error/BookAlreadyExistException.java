package com.example.demo.error;

import com.example.demo.persistence.model.Book;

public class BookAlreadyExistException extends RuntimeException {

    public BookAlreadyExistException(Book book) {
        super("The book " + book.getTitle() + " by " + book.getAuthor() + " already exist");
    }
}
