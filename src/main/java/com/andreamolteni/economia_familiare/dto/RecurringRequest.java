package com.andreamolteni.economia_familiare.dto;

import com.andreamolteni.economia_familiare.validation.Size12;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RecurringRequest(
        @NotBlank String type,
        @NotBlank String description,

        @NotNull @Size12 List<BigDecimal> amount, // può contenere null
        @NotNull @Size12 List<LocalDate> date,    // può contenere null

        @NotNull List<@Min(1) @Max(12) Integer> months,
        @Min(1) @Max(31) int dayOfTheMonth
) {}

