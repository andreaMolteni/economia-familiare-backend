package com.andreamolteni.economia_familiare.service;

import com.andreamolteni.economia_familiare.dto.FlattenedRow;
import com.andreamolteni.economia_familiare.dto.OverviewResponse;
import com.andreamolteni.economia_familiare.entity.*;
import com.andreamolteni.economia_familiare.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OverviewService {

    private final ExpenseRepository expenseRepo;
    private final IncomeRepository incomeRepo;
    private final RecurringExpenseRepository recurringExpenseRepo;
    private final RecurringIncomeRepository recurringIncomeRepo;
    @Value("${app.dev.artificial-delay-ms:0}")
    private long artificialDelayMs;

    public OverviewService(
            ExpenseRepository expenseRepo,
            IncomeRepository incomeRepo,
            RecurringExpenseRepository recurringExpenseRepo,
            RecurringIncomeRepository recurringIncomeRepo
    ) {
        this.expenseRepo = expenseRepo;
        this.incomeRepo = incomeRepo;
        this.recurringExpenseRepo = recurringExpenseRepo;
        this.recurringIncomeRepo = recurringIncomeRepo;
    }

    public OverviewResponse build(Long userId, LocalDate referenceDate, int closingDay, LocalDate asOf, BigDecimal availableBalance) {

        // simula latenza in ambiente dev
        if (artificialDelayMs > 0) {
            try {
                Thread.sleep(artificialDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        AccountingPeriod period = AccountingPeriod.of(referenceDate, closingDay);

        List<FlattenedRow> expenses = flattenExpenses(userId, period, asOf);
        List<FlattenedRow> income = flattenIncome(userId, period, asOf);

        BigDecimal expensesMonth = sum(expenses);
        BigDecimal expensesNotExpired = sumNotExpired(expenses);
        BigDecimal incomeMonth = sum(income);
        BigDecimal incomeNotExpired = sumNotExpired(income);

        OverviewResponse.Totals totals = new OverviewResponse.Totals(
                expensesMonth,
                expensesNotExpired,
                incomeMonth,
                incomeNotExpired,
                incomeMonth.subtract(expensesMonth),
                incomeNotExpired.subtract(expensesNotExpired)
        );

        return new OverviewResponse(period.start(), period.end(), expenses, income, totals, closingDay, availableBalance);
    }

    private List<FlattenedRow> flattenExpenses(Long userId, AccountingPeriod period, LocalDate asOf) {
        List<FlattenedRow> out = new ArrayList<>();

        // single
        var singles = expenseRepo.findByUser_IdAndDateBetweenOrderByDateAsc(userId, period.start(), period.end());
        for (Expense e : singles) {
            out.add(new FlattenedRow(
                    "single-" + e.getId(),
                    "single",
                    e.getId(),
                    e.getType(),
                    e.getDescription(),
                    e.getAmount(),         // se in Expense hai "value" come field java
                    e.getDate(),
                    e.getDate().isBefore(asOf)
            ));
        }

        // recurring: resolve by date in period
        var recurring = recurringExpenseRepo.findByUser_Id(userId);
        for (RecurringExpense r : recurring) {
            addRecurringRows(out, r.getId(), r.getType(), r.getDescription(), r.getAmount(), r.getDate(), period, asOf);
        }

        out.sort(Comparator.comparing(FlattenedRow::date).thenComparing(FlattenedRow::rowKey));
        return out;
    }

    private List<FlattenedRow> flattenIncome(Long userId, AccountingPeriod period, LocalDate asOf) {
        List<FlattenedRow> out = new ArrayList<>();

        var singles = incomeRepo.findByUser_IdAndDateBetweenOrderByDateAsc(userId, period.start(), period.end());
        for (Income i : singles) {
            out.add(new FlattenedRow(
                    "single-" + i.getId(),
                    "single",
                    i.getId(),
                    i.getType(),
                    i.getDescription(),
                    i.getAmount(),
                    i.getDate(),
                    i.getDate().isBefore(asOf)
            ));
        }

        var recurring = recurringIncomeRepo.findByUser_Id(userId);
        for (RecurringIncome r : recurring) {
            addRecurringRows(out, r.getId(), r.getType(), r.getDescription(), r.getAmount(), r.getDate(), period, asOf);
        }

        out.sort(Comparator.comparing(FlattenedRow::date).thenComparing(FlattenedRow::rowKey));
        return out;
    }

    private void addRecurringRows(
            List<FlattenedRow> out,
            Long recurringId,
            String type,
            String description,
            List<java.math.BigDecimal> amount,
            List<java.time.LocalDate> date,
            AccountingPeriod period,
            LocalDate asOf
    ) {
        // amount/date sono liste size 12 (con null ammessi)
        int n = Math.min(amount.size(), date.size());
        for (int idx = 0; idx < n; idx++) {
            LocalDate d = date.get(idx);
            if (d == null) continue;
            if (d.isBefore(period.start()) || d.isAfter(period.end())) continue;

            var a = amount.get(idx);
            if (a == null) continue;

            int month = d.getMonthValue(); // 1..12
            out.add(new FlattenedRow(
                    "recurring-" + recurringId + "-" + month,
                    "recurring",
                    recurringId,
                    type,
                    description,
                    a,
                    d,
                    d.isBefore(asOf)
            ));
        }
    }

    private BigDecimal sum(List<FlattenedRow> rows) {
        BigDecimal t = BigDecimal.ZERO;
        for (var r : rows) t = t.add(r.amount());
        return t;
    }

    private BigDecimal sumNotExpired(List<FlattenedRow> rows) {
        BigDecimal t = BigDecimal.ZERO;
        for (var r : rows) {
            if (!r.expired()) t = t.add(r.amount());
        }
        return t;
    }
}

