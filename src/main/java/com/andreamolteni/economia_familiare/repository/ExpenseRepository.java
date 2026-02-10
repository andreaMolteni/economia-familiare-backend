package com.andreamolteni.economia_familiare.repository;


import com.andreamolteni.economia_familiare.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser_IdOrderByDateAsc(Long userId);
    Optional<Expense> findByIdAndUser_Id(Long id, Long userId);
    List<Expense> findByUser_IdAndDateBetweenOrderByDateAsc(Long userId, LocalDate start, LocalDate end);
}
