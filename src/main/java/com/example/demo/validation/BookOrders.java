package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookOrdersValidator.class)
@Documented
public @interface BookOrders {
    String message() default "Invalid book sort pattern";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
