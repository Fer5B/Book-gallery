package com.example.demo.persistence.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

//import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    private static Validator validator;
    private LocalDate localDate;

    @BeforeEach
    public void setUp() {
        localDate = LocalDate.now();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void whenNotBlankValues_thenNoConstraintViolations() {
        Book book = Book.builder().title("Crimen y castigo").author("Fiódor Dostoyevski").price(BigDecimal.ONE).releaseDate(localDate).build();
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertEquals(0, violations.size());
    }

    @Test
    public void whenBlankValues_thenFourConstraintViolations() {
        Book book = Book.builder().title(" ").author("    ").price(null).releaseDate(null).build();
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertEquals(4, violations.size());
    }

    @Test
    public void whenSameObjects_thenAssertTrueAndEquals() {
        Book book1 = Book.builder().title("Orgullo y prejuicio").author("Jane Austen").price(new BigDecimal("2000")).releaseDate(localDate).build();
        Book book2 = Book.builder().title("En busca del tiempo perdido").author("Marcel Proust").price(new BigDecimal("1000")).releaseDate(localDate).build();

        assertTrue(book1.equals(book2));
        assertEquals(book1, book2);
    }

    @Test
    public void whenNoSameObjects_thenAssertFalseAndNotEquals() {
        Book book1 = Book.builder().title("Cien años de soledad").author("Gabriel García Márquez").price(new BigDecimal("2000")).releaseDate(LocalDate.now()).build();
        Book book2 = Book.builder().title("La telaraña de Carlota").author("E.B. White").price(new BigDecimal("1000")).releaseDate(localDate).build();

        assertFalse(book1.equals(book2));
        assertNotEquals(book1, book2);
    }

    @Test
    void testMatchRegexToSearchBookSort() {
        String input_1 = "title:ASC";
        String input_2 = "title:asc, ";
        String input_3 = "title:DESC, other:asc";
        String input_4 = "title:desc, author:ASC, price:DESC, releaseDate:asc";
        String input_5 = "title:desc, author:ASC, price:DESC, releaseDate:asc, title:ASC";

        String regex = "^((title|author|price|releaseDate):(?i)(ASC|DESC),\\p{Blank}?){0,3}" +
                "(title|author|price|releaseDate):(?i)(ASC|DESC)$";

        assertTrue(input_1.matches(regex));
        assertFalse(input_2.matches(regex));
        assertFalse(input_3.matches(regex));
        assertTrue(input_4.matches(regex));
        assertFalse(input_5.matches(regex));
    }

}