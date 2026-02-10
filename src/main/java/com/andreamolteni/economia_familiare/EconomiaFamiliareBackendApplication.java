package com.andreamolteni.economia_familiare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.andreamolteni.economia_familiare")
@EntityScan("com.andreamolteni.economia_familiare.entity")
@EnableJpaRepositories("com.andreamolteni.economia_familiare.repository")
public class EconomiaFamiliareBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EconomiaFamiliareBackendApplication.class, args);
	}

}
