package com.andreamolteni.economia_familiare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RecurringResponse(
        Long id,
        String type,
        String description,
        List<BigDecimal> amount,
        List<LocalDate> date,
        List<Integer> months,
        int dayOfTheMonth
) {}
