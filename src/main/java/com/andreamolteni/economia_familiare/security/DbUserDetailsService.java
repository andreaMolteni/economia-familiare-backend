package com.andreamolteni.economia_familiare.security;

import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public DbUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // ruolo semplice basato su tipoUtente (per ora)
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUserName())
                .password(u.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}
