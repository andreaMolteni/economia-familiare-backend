package com.andreamolteni.economia_familiare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FlattenedRow(
        String rowKey,          // "single-12" o "recurring-5-3"
        String source,          // "single" | "recurring"
        Long id,                // id entity sorgente
        String type,
        String description,
        BigDecimal amount,
        LocalDate date,
        boolean expired         // date < asOf
) {}

