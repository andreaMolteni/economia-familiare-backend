package com.andreamolteni.economia_familiare.security;

import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class CurrentUserService {

    private final UserRepository userRepo;

    public CurrentUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String username = auth.getName(); // BasicAuth username
        return userRepo.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
