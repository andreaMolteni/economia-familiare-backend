package com.andreamolteni.economia_familiare.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("ping ricevuto. Mi tengo scetato!");
        return ResponseEntity.ok("ok");
    }
}
