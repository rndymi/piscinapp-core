package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class InteractiveLoginConfiguration {

    @Bean
    @Order(2)
    SecurityFilterChain interactiveLoginSecurityFilterChain(
            HttpSecurity http
    ) {

        return http
                .securityMatcher(
                        "/login",
                        "/error"
                )
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(
                                                "/login",
                                                "/error"
                                        )
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                )
                .formLogin(
                        Customizer.withDefaults()
                )
                .build();
    }
}
