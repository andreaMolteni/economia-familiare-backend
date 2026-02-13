package com.andreamolteni.economia_familiare.security;

import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User u = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUserName())
                .password(u.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
    }
}
