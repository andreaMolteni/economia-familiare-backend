package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.dto.UpdateUserConfigRequest;
import com.andreamolteni.economia_familiare.dto.UserConfigDto;
import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.security.CurrentUserService;
import com.andreamolteni.economia_familiare.security.UserConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/me")
public class UserConfigController {

    private final CurrentUserService currentUserService;
    private final UserConfigService userConfigService;

    public UserConfigController(CurrentUserService currentUserService, UserConfigService userConfigService) {
        this.currentUserService = currentUserService;
        this.userConfigService = userConfigService;
    }

    @GetMapping("/config")
    public UserConfigDto getConfig() {
        User user = currentUserService.requireUser();
        var cfg = userConfigService.getOrCreate(user.getId());
        return new UserConfigDto(cfg.getClosingDay(), cfg.getAvailableBalance());
    }

    @PatchMapping("/config")
    public UserConfigDto patchConfig(@RequestBody UpdateUserConfigRequest req) {
        User user = currentUserService.requireUser();

        // validazione minima
        if (req.closingDay() != null && (req.closingDay() < 1 || req.closingDay() > 31)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "closingDay must be 1..31");
        }

        var cfg = userConfigService.update(user.getId(), req.closingDay(), req.availableBalance());
        return new UserConfigDto(cfg.getClosingDay(), cfg.getAvailableBalance());
    }
}
