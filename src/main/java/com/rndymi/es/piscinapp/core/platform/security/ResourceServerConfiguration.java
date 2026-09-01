package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerConfiguration {

    @Bean
    @Order(3)
    @SuppressWarnings("java:S4502")
    SecurityFilterChain
    resourceServerSecurityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter
                    jwtAuthenticationConverter,
            ApiAuthenticationEntryPoint
                    apiAuthenticationEntryPoint,
            ApiAccessDeniedHandler
                    apiAccessDeniedHandler
    ) {

        return http
                .csrf(
                        csrf ->
                                csrf
                                        .ignoringRequestMatchers(
                                                "/api/**"
                                        )
                )
                .sessionManagement(
                        session ->
                                session
                                        .sessionCreationPolicy(
                                                SessionCreationPolicy
                                                        .STATELESS
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
                                        .requestMatchers(
                                                "/api/v1/users/**",
                                                "/api/v1/employees/**"
                                        )
                                        .hasRole(
                                                "ADMIN"
                                        )
                                        .requestMatchers(
                                                "/api/v1/**"
                                        )
                                        .authenticated()
                                        .anyRequest()
                                        .authenticated()
                )
                .exceptionHandling(
                        exceptions ->
                                exceptions
                                        .accessDeniedHandler(
                                                apiAccessDeniedHandler
                                        )
                )
                .oauth2ResourceServer(
                        resourceServer ->
                                resourceServer
                                        .authenticationEntryPoint(
                                                apiAuthenticationEntryPoint
                                        )
                                        .jwt(
                                                jwt ->
                                                        jwt
                                                                .jwtAuthenticationConverter(
                                                                        jwtAuthenticationConverter
                                                                )
                                        )
                )
                .httpBasic(
                        AbstractHttpConfigurer::disable
                )
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .build();
    }

    @Bean
    JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter
                .setJwtGrantedAuthoritiesConverter(
                        new JwtRoleAuthoritiesConverter()
                );

        return converter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }
}
