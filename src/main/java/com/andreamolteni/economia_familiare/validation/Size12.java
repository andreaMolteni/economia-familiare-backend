package com.andreamolteni.economia_familiare.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Size12Validator.class)
public @interface Size12 {
    String message() default "Must have length 12";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
