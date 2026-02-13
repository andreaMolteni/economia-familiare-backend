package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.ExpenseResponse;
import com.andreamolteni.economia_familiare.dto.IncomeRequest;
import com.andreamolteni.economia_familiare.dto.IncomeResponse;
import com.andreamolteni.economia_familiare.entity.Expense;
import com.andreamolteni.economia_familiare.entity.Income;
import com.andreamolteni.economia_familiare.repository.IncomeRepository;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/me/income")
public class IncomeMeController {

    private final CurrentUserService currentUser;
    private final IncomeRepository incomeRepo;

    public IncomeMeController(CurrentUserService currentUser, IncomeRepository incomeRepo) {
        this.currentUser = currentUser;
        this.incomeRepo = incomeRepo;
    }

    @GetMapping
    public List<IncomeResponse> list() {
        var u = currentUser.requireUser();
        return incomeRepo.findByUser_IdOrderByDateAsc(u.getId()).stream()
                .map(i -> new IncomeResponse(i.getId(), i.getType(), i.getDescription(), i.getAmount(), i.getDate()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncomeResponse create(@Valid @RequestBody IncomeRequest req) {
        var u = currentUser.requireUser();

        Income i = new Income();
        i.setUser(u);
        i.setType(req.type().trim());
        i.setDescription(req.description().trim());
        i.setAmount(req.amount());
        i.setDate(req.date());

        Income saved = incomeRepo.save(i);
        return new IncomeResponse(saved.getId(), saved.getType(), saved.getDescription(), saved.getAmount(), saved.getDate());
    }

    @PutMapping("/{incomeId}")
    public IncomeResponse update(@PathVariable Long incomeId, @Valid @RequestBody IncomeRequest req) {
        var u = currentUser.requireUser();

        Income i = incomeRepo.findByIdAndUser_Id(incomeId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income non trovata"));

        i.setType(req.type().trim());
        i.setDescription(req.description().trim());
        i.setAmount(req.amount());
        i.setDate(req.date());

        Income saved = incomeRepo.save(i);
        return new IncomeResponse(saved.getId(), saved.getType(), saved.getDescription(), saved.getAmount(), saved.getDate());
    }

    @DeleteMapping("/{incomeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long incomeId) {
        var u = currentUser.requireUser();

        Income i = incomeRepo.findByIdAndUser_Id(incomeId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income non trovata"));

        incomeRepo.delete(i);
    }

    @GetMapping("/{incomeId}")
    public IncomeResponse get(@PathVariable Long incomeId) {
        var u = currentUser.requireUser();

        Income i = incomeRepo.findByIdAndUser_Id(incomeId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income non trovata"));

        return new IncomeResponse(i.getId(), i.getType(), i.getDescription(), i.getAmount(), i.getDate());
    }
}

