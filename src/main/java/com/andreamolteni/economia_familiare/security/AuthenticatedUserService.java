package com.andreamolteni.economia_familiare.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.repository.UserRepository;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

