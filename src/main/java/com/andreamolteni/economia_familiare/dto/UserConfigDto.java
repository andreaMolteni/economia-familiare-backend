package com.andreamolteni.economia_familiare.dto;

import java.math.BigDecimal;

public record UserConfigDto(
        int closingDay,
        BigDecimal availableBalance
) {}
