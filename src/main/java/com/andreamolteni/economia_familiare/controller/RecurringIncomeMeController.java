package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.RecurringRequest;
import com.andreamolteni.economia_familiare.dto.RecurringResponse;
import com.andreamolteni.economia_familiare.entity.RecurringIncome;
import com.andreamolteni.economia_familiare.repository.RecurringIncomeRepository;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/me/recurring-income")
public class RecurringIncomeMeController {

    private final CurrentUserService currentUser;
    private final RecurringIncomeRepository repo;

    public RecurringIncomeMeController(CurrentUserService currentUser, RecurringIncomeRepository repo) {
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

        RecurringIncome r = new RecurringIncome();
        r.setUser(u);
        r.setType(req.type().trim());
        r.setDescription(req.description().trim());
        r.setAmount(req.amount());
        r.setDate(req.date());
        r.setMonths(req.months().stream().distinct().sorted().toList());
        r.setDayOfTheMonth(req.dayOfTheMonth());

        RecurringIncome saved = repo.save(r);
        return new RecurringResponse(saved.getId(), saved.getType(), saved.getDescription(),
                saved.getAmount(), saved.getDate(), saved.getMonths(), saved.getDayOfTheMonth());
    }

    @PutMapping("/{id}")
    public RecurringResponse update(@PathVariable Long id, @Valid @RequestBody RecurringRequest req) {
        var u = currentUser.requireUser();

        validateMonths(req.months());
        validateMonthDataCoherence(req.months(), req.amount(), req.date());

        RecurringIncome r = repo.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurring income non trovata"));

        r.setType(req.type().trim());
        r.setDescription(req.description().trim());
        r.setAmount(req.amount());
        r.setDate(req.date());
        r.setMonths(req.months().stream().distinct().sorted().toList());
        r.setDayOfTheMonth(req.dayOfTheMonth());

        RecurringIncome saved = repo.save(r);
        return new RecurringResponse(saved.getId(), saved.getType(), saved.getDescription(),
                saved.getAmount(), saved.getDate(), saved.getMonths(), saved.getDayOfTheMonth());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var u = currentUser.requireUser();
        RecurringIncome r = repo.findByIdAndUser_Id(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurring income non trovata"));
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

