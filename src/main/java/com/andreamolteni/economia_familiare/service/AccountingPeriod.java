package com.andreamolteni.economia_familiare.service;

import java.time.LocalDate;
import java.time.YearMonth;

public record AccountingPeriod(LocalDate start, LocalDate end) {

    public static AccountingPeriod of(LocalDate referenceDate, int closingDay) {
        if (closingDay < 1 || closingDay > 31) {
            throw new IllegalArgumentException("closingDay must be 1..31");
        }

        int refDay = referenceDate.getDayOfMonth();

        if (refDay > closingDay) {
            LocalDate start = safeDate(referenceDate.getYear(), referenceDate.getMonthValue(), closingDay + 1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            return new AccountingPeriod(start, end);
        } else {
            LocalDate end = safeDate(referenceDate.getYear(), referenceDate.getMonthValue(), closingDay);
            LocalDate start = end.minusMonths(1).plusDays(1);
            return new AccountingPeriod(start, end);
        }
    }

    private static LocalDate safeDate(int year, int month, int day) {
        YearMonth ym = YearMonth.of(year, month);
        int safeDay = Math.min(Math.max(day, 1), ym.lengthOfMonth());
        return ym.atDay(safeDay);
    }
}
