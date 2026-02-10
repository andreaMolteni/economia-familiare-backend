package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.ExpenseRequest;
import com.andreamolteni.economia_familiare.dto.ExpenseResponse;
import com.andreamolteni.economia_familiare.entity.Expense;
import com.andreamolteni.economia_familiare.repository.ExpenseRepository;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/me/expenses")
public class ExpenseMeController {

    private final CurrentUserService currentUser;
    private final ExpenseRepository expenseRepo;

    public ExpenseMeController(CurrentUserService currentUser, ExpenseRepository expenseRepo) {
        this.currentUser = currentUser;
        this.expenseRepo = expenseRepo;
    }

    @GetMapping
    public List<ExpenseResponse> list() {
        var u = currentUser.requireUser();
        return expenseRepo.findByUser_IdOrderByDateAsc(u.getId()).stream()
                .map(e -> new ExpenseResponse(e.getId(), u.getId(), e.getType(), e.getDescription(), e.getValue(), e.getDate()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest req) {
        var u = currentUser.requireUser();

        Expense e = new Expense();
        e.setUser(u);
        e.setType(req.type().trim());
        e.setDescription(req.description().trim());
        e.setValue(req.amount());
        e.setDate(req.date());

        Expense saved = expenseRepo.save(e);
        return new ExpenseResponse(saved.getId(), u.getId(), saved.getType(), saved.getDescription(), saved.getValue(), saved.getDate());
    }

    @PutMapping("/{expenseId}")
    public ExpenseResponse update(@PathVariable Long expenseId, @Valid @RequestBody ExpenseRequest req) {
        var u = currentUser.requireUser();

        Expense e = expenseRepo.findByIdAndUser_Id(expenseId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense non trovata"));

        e.setType(req.type().trim());
        e.setDescription(req.description().trim());
        e.setValue(req.amount());
        e.setDate(req.date());

        Expense saved = expenseRepo.save(e);
        return new ExpenseResponse(saved.getId(), u.getId(), saved.getType(), saved.getDescription(), saved.getValue(), saved.getDate());
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long expenseId) {
        var u = currentUser.requireUser();
        Expense e = expenseRepo.findByIdAndUser_Id(expenseId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense non trovata"));
        expenseRepo.delete(e);
    }
}
