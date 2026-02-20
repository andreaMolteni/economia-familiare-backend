package com.andreamolteni.economia_familiare.repository;

import com.andreamolteni.economia_familiare.entity.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
}
