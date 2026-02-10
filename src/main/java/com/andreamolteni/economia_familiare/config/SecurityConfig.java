package com.andreamolteni.economia_familiare.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // registrazione aperta
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers("/h2/**").permitAll()// attenzione: vale per TUTTI i metodi se non restringi
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // se vuoi solo POST aperto, vedi nota sotto
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));


        return http.build();
    }
}
