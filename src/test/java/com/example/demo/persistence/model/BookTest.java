package com.example.demo.persistence.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

//import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    private static Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void whenNotBlankValues_thenNoConstraintViolations() {
        Book book = Book.builder().title("Caperucita roja").author("Don Ramón").price(BigDecimal.ONE).releaseDate("1960").build();
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertEquals(0, violations.size());
    }

    @Test
    public void whenBlankValues_thenFourConstraintViolations() {
        Book book = Book.builder().title(" ").author("     ").price(null).releaseDate("").build();
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertEquals(4, violations.size());
    }

    @Test
    public void whenSameObjects_thenAssertTrueAndEquals() {
        Book book1 = Book.builder().title("Caperucita roja").author("Don Ramón").price(new BigDecimal("2000")).releaseDate("1960").build();
        Book book2 = Book.builder().title("Caperucita roja").author("Don Ramón").price(new BigDecimal("1000")).releaseDate("1960").build();

        assertTrue(book1.equals(book2));
        assertEquals(book1, book2);
    }

    @Test
    public void whenNoSameObjects_thenAssertFalseAndNotEquals() {
        Book book1 = Book.builder().title("El zorro y el sabueso").author("El abuelito de Heidi").price(new BigDecimal("2000")).releaseDate("1640").build();
        Book book2 = Book.builder().title("Caperucita roja").author("Don Ramón").price(new BigDecimal("1000")).releaseDate("1960").build();

        assertFalse(book1.equals(book2));
        assertNotEquals(book1, book2);
    }



}