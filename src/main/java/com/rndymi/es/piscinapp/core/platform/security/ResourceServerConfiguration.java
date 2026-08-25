package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerConfiguration {

    @Bean
    @Order(2)
    SecurityFilterChain resourceServerSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(
                        csrf ->
                                csrf.ignoringRequestMatchers(
                                        "/api/**"
                                )
                )
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(
                                                "/actuator/health",
                                                "/v3/api-docs/**",
                                                "/swagger-ui.html",
                                                "/swagger-ui/**"
                                        )
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                )
                .oauth2ResourceServer(
                        resourceServer ->
                                resourceServer.jwt(
                                        Customizer.withDefaults()
                                )
                )
                .httpBasic(
                        httpBasic ->
                                httpBasic.disable()
                )
                .formLogin(
                        formLogin ->
                                formLogin.disable()
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }
}
