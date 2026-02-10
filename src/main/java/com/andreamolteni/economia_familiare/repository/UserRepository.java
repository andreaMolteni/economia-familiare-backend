package com.andreamolteni.economia_familiare.repository;

import com.andreamolteni.economia_familiare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);
}
