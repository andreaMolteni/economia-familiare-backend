package com.andreamolteni.economia_familiare.repository;

import com.andreamolteni.economia_familiare.entity.RecurringIncome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecurringIncomeRepository extends JpaRepository<RecurringIncome, Long> {
    List<RecurringIncome> findByUser_Id(Long userId);
    Optional<RecurringIncome> findByIdAndUser_Id(Long id, Long userId);
}

