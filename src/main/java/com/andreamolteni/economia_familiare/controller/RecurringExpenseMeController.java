package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.RecurringRequest;
import com.andreamolteni.economia_familiare.dto.RecurringResponse;
import com.andreamolteni.economia_familiare.entity.RecurringExpense;
import com.andreamolteni.economia_familiare.repository.RecurringExpenseRepository;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/me/recurring-expenses")
public class RecurringExpenseMeController {

    private final CurrentUserService currentUser;
    private final RecurringExpenseRepository repo;

    public RecurringExpenseMeController(CurrentUserService currentUser, RecurringExpenseRepository repo) {
        this.currentUser = currentUser;
        this.repo = repo;
    }

    @GetMapping
    public List<RecurringResponse> list() {
        var u = currentUser.requireUser();
        return repo.findByUser_Id(u.getId()).stream()
                .map(r -> new RecurringResponse(r.getId(), r.getType(), r.getDescription(),
                        r.getAmount(), r.getDate(), r.getMonths(), r.getDayOfTheMonth()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecurringResponse create(@Valid @RequestBody RecurringRequest req) {
        var u = currentUser.requireUser();

        validateMonths(req.months());
        validateMonthDataCoherence(req.months(), req.amount(), req.date());

        RecurringExpense r = new RecurringExpense();
        r.setUser(u);
        r.setType(req.type().trim());
        r.setDescription(req.description().trim());
        r.setAmount(req.amount());
        r.setDate(req.date());
        r.setMonths(req.months().stream().distinct().sorted().toList());
        r.setDayOfTheMonth(req.dayOfTheMonth());

        RecurringExpense saved = repo.save(r);
        return new RecurringResponse(saved.getId(), saved.getType(), saved.getDescription(),
                saved.getAmount(), saved.getDate(), saved.getMonths(), saved.getDayOfTheMonth());
    }

    @PutMapping("/{id}")
    public RecurringResponse update(@PathVariable Long id, @Valid @RequestBody RecurringRequest req) {
        var u = currentUser.requireUser();

        validateMonths(req.months());
        validateMonthDataCoherence(req.months(), req.amount(), req.date());

        RecurringExpense r = repo.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurring expense non trovata"));

        r.setType(req.type().trim());
        r.setDescription(req.description().trim());
        r.setAmount(req.amount());
        r.setDate(req.date());
        r.setMonths(req.months().stream().distinct().sorted().toList());
        r.setDayOfTheMonth(req.dayOfTheMonth());

        RecurringExpense saved = repo.save(r);
        return new RecurringResponse(saved.getId(), saved.getType(), saved.getDescription(),
                saved.getAmount(), saved.getDate(), saved.getMonths(), saved.getDayOfTheMonth());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var u = currentUser.requireUser();
        RecurringExpense r = repo.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurring expense non trovata"));
        repo.delete(r);
    }

    private void validateMonths(List<Integer> months) {
        if (months == null || months.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "months deve contenere almeno un mese");
        }
        for (Integer m : months) {
            if (m == null || m < 1 || m > 12) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "months contiene valori non validi");
            }
        }
    }

    // coerenza: per ogni mese attivo ci aspettiamo amount/date valorizzati nell’indice (mese-1)
    private void validateMonthDataCoherence(List<Integer> months, List<?> amount, List<?> date) {
        for (Integer m : months) {
            int idx = m - 1;
            if (amount.get(idx) == null || date.get(idx) == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Per i mesi attivi amount[idx] e date[idx] devono essere valorizzati (idx = mese-1)"
                );
            }
        }
    }
}
