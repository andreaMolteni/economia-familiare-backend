package com.andreamolteni.economia_familiare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OverviewResponse(
        LocalDate periodStart,
        LocalDate periodEnd,
        List<FlattenedRow> expenses,
        List<FlattenedRow> income,
        Totals totals,
        int closingDay,
        BigDecimal availableBalance
) {
    public record Totals(
            BigDecimal expensesMonth,
            BigDecimal expensesNotExpired,
            BigDecimal incomeMonth,
            BigDecimal incomeNotExpired,
            BigDecimal balanceMonth,
            BigDecimal balanceNotExpired
    ) {}
}
