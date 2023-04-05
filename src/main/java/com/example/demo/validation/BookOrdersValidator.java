package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookOrdersValidator implements ConstraintValidator<BookOrders, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile("^((title|author|price|releaseDate):(?i)(ASC|DESC),\\p{Blank}?){0,3}" +
                "((title|author|price|releaseDate):(?i)(ASC|DESC))|\\p{Space}*$");

        return pattern.matcher(s).matches();
    }
}
