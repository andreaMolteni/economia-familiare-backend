package com.andreamolteni.economia_familiare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String type,
        String description,
        BigDecimal amount,
        LocalDate date
) {}