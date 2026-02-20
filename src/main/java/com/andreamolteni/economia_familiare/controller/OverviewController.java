package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.OverviewResponse;
import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.entity.UserConfig;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import com.andreamolteni.economia_familiare.security.UserConfigService;
import com.andreamolteni.economia_familiare.service.OverviewService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/me")
public class OverviewController {

    private final CurrentUserService currentUser;
    private final OverviewService overviewService;
    private final UserConfigService userConfigService;

    public OverviewController(CurrentUserService currentUser, OverviewService overviewService,UserConfigService userConfigService) {
        this.currentUser = currentUser;
        this.overviewService = overviewService;
        this.userConfigService = userConfigService;
    }

    @GetMapping("/overview")
    public OverviewResponse overview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate,
            @RequestParam(required = false) Integer closingDay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf
    ) {
        User user = currentUser.requireUser();
        UserConfig cfg = userConfigService.getOrCreate(user.getId());
        LocalDate effectiveAsOf = (asOf != null) ? asOf : LocalDate.now();
        return overviewService.build(user.getId(), referenceDate, cfg.getClosingDay(), effectiveAsOf, cfg.getAvailableBalance());
    }
}
