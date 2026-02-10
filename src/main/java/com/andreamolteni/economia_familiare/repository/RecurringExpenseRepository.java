package com.andreamolteni.economia_familiare.repository;

import com.andreamolteni.economia_familiare.entity.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findByUser_Id(Long userId);
    Optional<RecurringExpense> findByIdAndUser_Id(Long id, Long userId);
}
