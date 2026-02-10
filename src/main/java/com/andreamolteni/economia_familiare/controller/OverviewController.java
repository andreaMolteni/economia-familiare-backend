package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.OverviewResponse;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import com.andreamolteni.economia_familiare.service.OverviewService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/me")
public class OverviewController {

    private final CurrentUserService currentUser;
    private final OverviewService overviewService;

    public OverviewController(CurrentUserService currentUser, OverviewService overviewService) {
        this.currentUser = currentUser;
        this.overviewService = overviewService;
    }

    @GetMapping("/overview")
    public OverviewResponse overview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate,
            @RequestParam int closingDay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf
    ) {
        var u = currentUser.requireUser();
        LocalDate effectiveAsOf = (asOf != null) ? asOf : LocalDate.now();
        return overviewService.build(u.getId(), referenceDate, closingDay, effectiveAsOf);
    }
}
