package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(
        CorsProperties.class
)
public class CorsConfiguration {

    private static final Set<String>
            SUPPORTED_METHODS =
            Set.of(
                    "GET",
                    "POST",
                    "PUT",
                    "PATCH",
                    "DELETE",
                    "OPTIONS"
            );

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            CorsProperties properties
    ) {

        validate(
                properties
        );

        org.springframework.web.cors.CorsConfiguration
                configuration =
                new org.springframework.web.cors.CorsConfiguration();

        configuration.setAllowedOrigins(
                properties.getAllowedOrigins()
                        .stream()
                        .filter(
                                origin ->
                                        origin != null
                                                && !origin.isBlank()
                        )
                        .toList()
        );

        configuration.setAllowedMethods(
                properties.getAllowedMethods()
                        .stream()
                        .map(
                                method ->
                                        method.toUpperCase(
                                                Locale.ROOT
                                        )
                        )
                        .toList()
        );

        configuration.setAllowedHeaders(
                properties.getAllowedHeaders()
                        .stream()
                        .toList()
        );

        configuration.setAllowCredentials(
                properties.isAllowCredentials()
        );

        configuration.setMaxAge(
                properties.getMaxAge()
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    private void validate(
            CorsProperties properties
    ) {

        properties.getAllowedOrigins()
                .stream()
                .filter(
                        origin ->
                                origin != null
                                        && !origin.isBlank()
                )
                .forEach(
                        this::validateOrigin
                );

        if (
                properties.getAllowedMethods()
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "CORS allowed-methods must not be empty"
            );
        }

        Set<String> normalizedMethods =
                properties.getAllowedMethods()
                        .stream()
                        .map(
                                method ->
                                        method.toUpperCase(
                                                Locale.ROOT
                                        )
                        )
                        .collect(
                                java.util.stream.Collectors.toSet()
                        );

        for (
                String method
                : normalizedMethods
        ) {

            if (
                    !SUPPORTED_METHODS.contains(
                            method
                    )
            ) {

                throw new IllegalStateException(
                        "Unsupported CORS method: "
                                + method
                );
            }
        }

        if (
                !normalizedMethods.contains(
                        "OPTIONS"
                )
        ) {

            throw new IllegalStateException(
                    "CORS allowed-methods must include OPTIONS"
            );
        }

        if (
                properties.getAllowedHeaders()
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "CORS allowed-headers must not be empty"
            );
        }

        if (
                properties.getAllowedHeaders()
                        .contains("*")
        ) {

            throw new IllegalStateException(
                    "CORS allowed-headers must be explicit"
            );
        }

        if (
                properties.getMaxAge() == null
                        || properties.getMaxAge()
                        .isNegative()
        ) {

            throw new IllegalStateException(
                    "CORS max-age must not be negative"
            );
        }
    }

    private void validateOrigin(
            String origin
    ) {

        if (origin.contains("*")) {

            throw new IllegalStateException(
                    "CORS origins must not contain wildcards"
            );
        }

        try {

            URI uri =
                    new URI(
                            origin
                    );

            boolean supportedScheme =
                    "http".equalsIgnoreCase(
                            uri.getScheme()
                    )
                            || "https".equalsIgnoreCase(
                            uri.getScheme()
                    );

            boolean hasPath =
                    uri.getPath() != null
                            && !uri.getPath().isEmpty();

            if (
                    !uri.isAbsolute()
                            || uri.getHost() == null
                            || !supportedScheme
                            || hasPath
                            || uri.getQuery() != null
                            || uri.getFragment() != null
                            || uri.getUserInfo() != null
            ) {

                throw new IllegalStateException(
                        "CORS allowed origin must contain "
                                + "only scheme, host and optional port: "
                                + origin
                );
            }
        }
        catch (URISyntaxException exception) {

            throw new IllegalStateException(
                    "Invalid CORS allowed origin: "
                            + origin,
                    exception
            );
        }
    }
}
