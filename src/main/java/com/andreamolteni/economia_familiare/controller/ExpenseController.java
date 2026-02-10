package com.andreamolteni.economia_familiare.controller;


import com.andreamolteni.economia_familiare.dto.ExpenseRequest;
import com.andreamolteni.economia_familiare.dto.ExpenseResponse;
import com.andreamolteni.economia_familiare.entity.Expense;
import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.repository.ExpenseRepository;
import com.andreamolteni.economia_familiare.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/expenses")
public class ExpenseController {

    private final UserRepository userRepo;
    private final ExpenseRepository expenseRepo;

    public ExpenseController(UserRepository userRepo, ExpenseRepository expenseRepo) {
        this.userRepo = userRepo;
        this.expenseRepo = expenseRepo;
    }

    @GetMapping
    public List<ExpenseResponse> list(@PathVariable Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User non trovato");
        }
        return expenseRepo.findByUser_IdOrderByDateAsc(userId).stream()
                .map(e -> new ExpenseResponse(e.getId(), userId, e.getType(), e.getDescription(), e.getValue(), e.getDate()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@PathVariable Long userId, @Valid @RequestBody ExpenseRequest req) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User non trovato"));

        Expense e = new Expense();
        e.setUser(u);
        e.setType(req.type().trim());
        e.setDescription(req.description().trim());
        e.setValue(req.amount());
        e.setDate(req.date());

        Expense saved = expenseRepo.save(e);
        return new ExpenseResponse(saved.getId(), userId, saved.getType(), saved.getDescription(), saved.getValue(), saved.getDate());
    }

    @GetMapping("/{expenseId}")
    public ExpenseResponse get(@PathVariable Long userId, @PathVariable Long expenseId) {
        Expense e = expenseRepo.findByIdAndUser_Id(expenseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense non trovata"));
        return new ExpenseResponse(e.getId(), userId, e.getType(), e.getDescription(), e.getValue(), e.getDate());
    }

    @PutMapping("/{expenseId}")
    public ExpenseResponse update(@PathVariable Long userId, @PathVariable Long expenseId, @Valid @RequestBody ExpenseRequest req) {
        Expense e = expenseRepo.findByIdAndUser_Id(expenseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense non trovata"));

        e.setType(req.type().trim());
        e.setDescription(req.description().trim());
        e.setValue(req.amount());
        e.setDate(req.date());

        Expense saved = expenseRepo.save(e);
        return new ExpenseResponse(saved.getId(), userId, saved.getType(), saved.getDescription(), saved.getValue(), saved.getDate());
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long expenseId) {
        Expense e = expenseRepo.findByIdAndUser_Id(expenseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense non trovata"));
        expenseRepo.delete(e);
    }
}
