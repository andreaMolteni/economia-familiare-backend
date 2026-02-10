package com.andreamolteni.economia_familiare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank String type,
        @NotBlank String description,
        @NotNull BigDecimal amount,
        @NotNull LocalDate date
) {}
