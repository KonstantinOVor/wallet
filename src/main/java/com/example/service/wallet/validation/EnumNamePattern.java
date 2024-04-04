package com.example.service.wallet.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumNamePatternValidator.class)
public @interface EnumNamePattern {
    String message() default "Invalid operation type. Valid operation types are: DEPOSIT, WITHDRAW";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
