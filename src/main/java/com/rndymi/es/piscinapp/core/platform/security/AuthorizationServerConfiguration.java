package com.rndymi.es.piscinapp.core.platform.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
public class AuthorizationServerConfiguration {

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http
    ) {

        http.oauth2AuthorizationServer(
                authorizationServer -> {
                    http.securityMatcher(
                            authorizationServer
                                    .getEndpointsMatcher()
                    );

                    authorizationServer.oidc(
                            Customizer.withDefaults()
                    );
                }
        );

        http.authorizeHttpRequests(
                authorize ->
                        authorize
                                .anyRequest()
                                .authenticated()
        );

        http.exceptionHandling(
                exceptions ->
                        exceptions
                                .defaultAuthenticationEntryPointFor(
                                        new LoginUrlAuthenticationEntryPoint(
                                                "/login"
                                        ),
                                        new MediaTypeRequestMatcher(
                                                MediaType.TEXT_HTML
                                        )
                                )
        );

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(
            JWKSource<SecurityContext> jwkSource,
            @Value("${piscinapp.security.issuer}")
            String issuer
    ) {

        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder
                        .withJwkSource(
                                jwkSource
                        )
                        .build();

        jwtDecoder.setJwtValidator(
                JwtValidators
                        .createDefaultWithIssuer(
                                issuer
                        )
        );

        return jwtDecoder;
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings(
            @Value("${piscinapp.security.issuer}")
            String issuer
    ) {

        return AuthorizationServerSettings
                .builder()
                .issuer(issuer)
                .build();
    }
}
