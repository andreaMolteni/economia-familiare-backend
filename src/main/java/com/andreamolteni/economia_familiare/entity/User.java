package com.andreamolteni.economia_familiare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String nome;
    @Column(nullable=false) private String cognome;

    @Column(nullable=false, unique=true) private String email;

    @Column(name="user_name", nullable=false, unique=true)
    private String userName;

    @Column(name="password_hash", nullable=false)
    private String passwordHash;

    @Column(name="tipo_utente", nullable=false)
    private int tipoUtente;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public int getTipoUtente() { return tipoUtente; }
    public void setTipoUtente(int tipoUtente) { this.tipoUtente = tipoUtente; }
}