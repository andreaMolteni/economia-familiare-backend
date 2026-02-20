package com.andreamolteni.economia_familiare.dto;

import java.math.BigDecimal;

public record UpdateUserConfigRequest(
        Integer closingDay,
        BigDecimal availableBalance
) {}
