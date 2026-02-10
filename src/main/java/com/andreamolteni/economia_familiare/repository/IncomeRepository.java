package com.andreamolteni.economia_familiare.repository;

import com.andreamolteni.economia_familiare.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser_IdOrderByDateAsc(Long userId);
    Optional<Income> findByIdAndUser_Id(Long id, Long userId);
    List<Income> findByUser_IdAndDateBetweenOrderByDateAsc(Long userId, LocalDate start, LocalDate end);
}
