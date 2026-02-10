package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.security.CurrentUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {

    private final CurrentUserService currentUser;

    public MeController(CurrentUserService currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping
    public Object me() {
        var u = currentUser.requireUser();
        return new Object() {
            public final Long id = u.getId();
            public final String nome = u.getNome();
            public final String cognome = u.getCognome();
            public final String email = u.getEmail();
            public final String userName = u.getUserName();
            public final int tipoUtente = u.getTipoUtente();
        };
    }
}
