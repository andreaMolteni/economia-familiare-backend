package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.CreateUserRequest;
import com.andreamolteni.economia_familiare.dto.UserResponse;
import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
        if (userRepo.existsByEmail(req.email().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già esistente");
        }
        if (userRepo.existsByUserName(req.userName().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username già esistente");
        }

        User u = new User();
        u.setNome(req.nome().trim());
        u.setCognome(req.cognome().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setUserName(req.userName().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setTipoUtente(req.tipoUtente());

        User saved = userRepo.save(u);
        return new UserResponse(saved.getId(), saved.getNome(), saved.getCognome(), saved.getEmail(), saved.getUserName(), saved.getTipoUtente());
    }

    @GetMapping
    public List<UserResponse> list() {
        return userRepo.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getNome(), u.getCognome(), u.getEmail(), u.getUserName(), u.getTipoUtente()))
                .toList();
    }

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User non trovato"));
        return new UserResponse(u.getId(), u.getNome(), u.getCognome(), u.getEmail(), u.getUserName(), u.getTipoUtente());
    }
}
