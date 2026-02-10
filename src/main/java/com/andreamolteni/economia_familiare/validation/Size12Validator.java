package com.andreamolteni.economia_familiare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class Size12Validator implements ConstraintValidator<Size12, List<?>> {
    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        return value != null && value.size() == 12;
    }
}
