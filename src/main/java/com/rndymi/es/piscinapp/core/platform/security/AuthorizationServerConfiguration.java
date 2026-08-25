package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthorizationServerConfiguration {

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http.oauth2AuthorizationServer(
                authorizationServer ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())
        );

        http.authorizeHttpRequests(
                authorize ->
                        authorize
                                .anyRequest()
                                .authenticated()
        );

        return http.build();
    }

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        return new BootstrapRegisteredClientRepository();
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings(
            @Value("${piscinapp.security.issuer}")
            String issuer
    ) {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }
}
