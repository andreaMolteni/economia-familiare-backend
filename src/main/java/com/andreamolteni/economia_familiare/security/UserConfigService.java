package com.andreamolteni.economia_familiare.security;

import com.andreamolteni.economia_familiare.entity.User;
import com.andreamolteni.economia_familiare.entity.UserConfig;
import com.andreamolteni.economia_familiare.repository.UserConfigRepository;
import com.andreamolteni.economia_familiare.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class UserConfigService {

    private final UserConfigRepository userConfigRepository;
    private final UserRepository userRepository;

    public UserConfigService(UserConfigRepository userConfigRepository,
                             UserRepository userRepository) {
        this.userConfigRepository = userConfigRepository;
        this.userRepository = userRepository;
    }

    /**
     * Restituisce la config dell’utente.
     * Se non esiste, la crea con valori di default.
     */
    @Transactional
    public UserConfig getOrCreate(Long userId) {
        return userConfigRepository.findById(userId)
                .orElseGet(() -> createDefault(userId));
    }

    private UserConfig createDefault(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        UserConfig config = new UserConfig();
        config.setUser(user);
        config.setClosingDay(5); // default
        config.setAvailableBalance(BigDecimal.ZERO);

        return userConfigRepository.save(config);
    }

    /**
     * Aggiorna solo i campi presenti (PATCH semantics)
     */
    @Transactional
    public UserConfig update(Long userId,
                             Integer closingDay,
                             BigDecimal availableBalance) {

        UserConfig config = getOrCreate(userId);

        if (closingDay != null) {
            if (closingDay < 1 || closingDay > 31) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "closingDay must be between 1 and 31"
                );
            }
            config.setClosingDay(closingDay);
        }

        if (availableBalance != null) {
            config.setAvailableBalance(availableBalance);
        }

        return userConfigRepository.save(config);
    }
}

