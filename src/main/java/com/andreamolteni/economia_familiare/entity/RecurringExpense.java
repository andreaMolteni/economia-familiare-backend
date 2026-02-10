package com.andreamolteni.economia_familiare.entity;

import com.andreamolteni.economia_familiare.persistence.RecurringConverters;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "recurring_expenses")
public class RecurringExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String type;

    @Column(nullable = false, length = 500)
    private String description;

    @Convert(converter = RecurringConverters.AmountsJsonConverter.class)
    @Column(name = "amount_json", nullable = false, length = 10000)
    private List<BigDecimal> amount; // size 12, con null ammessi

    @Convert(converter = RecurringConverters.DatesJsonConverter.class)
    @Column(name = "date_json", nullable = false, length = 10000)
    private List<LocalDate> date; // size 12, con null ammessi

    @Convert(converter = RecurringConverters.MonthsJsonConverter.class)
    @Column(name = "months_json", nullable = false, length = 1000)
    private List<Integer> months; // 1..12

    @Column(name = "day_of_the_month", nullable = false)
    private int dayOfTheMonth;

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<BigDecimal> getAmount() { return amount; }
    public void setAmount(List<BigDecimal> amount) { this.amount = amount; }

    public List<LocalDate> getDate() { return date; }
    public void setDate(List<LocalDate> date) { this.date = date; }

    public List<Integer> getMonths() { return months; }
    public void setMonths(List<Integer> months) { this.months = months; }

    public int getDayOfTheMonth() { return dayOfTheMonth; }
    public void setDayOfTheMonth(int dayOfTheMonth) { this.dayOfTheMonth = dayOfTheMonth; }
}
