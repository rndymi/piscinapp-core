package com.rndymi.es.piscinapp.core.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsConfigurationTest {

    private final CorsConfiguration configuration =
            new CorsConfiguration();

    @Test
    void shouldCreateCorsConfigurationFromValidProperties() {

        CorsProperties properties =
                validProperties();

        properties.setAllowCredentials(
                true
        );

        CorsConfigurationSource source =
                configuration.corsConfigurationSource(
                        properties
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest(
                        "OPTIONS",
                        "/api/v1/me"
                );

        org.springframework.web.cors.CorsConfiguration resolved =
                source.getCorsConfiguration(
                        request
                );

        assertThat(
                resolved
        )
                .isNotNull();

        assertThat(
                resolved.getAllowedOrigins()
        )
                .containsExactly(
                        "http://localhost:4200"
                );

        assertThat(
                resolved.getAllowedMethods()
        )
                .containsExactlyInAnyOrder(
                        "GET",
                        "POST",
                        "OPTIONS"
                );

        assertThat(
                resolved.getAllowedHeaders()
        )
                .containsExactlyInAnyOrder(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                );

        assertThat(
                resolved.getAllowCredentials()
        )
                .isTrue();

        assertThat(
                resolved.getMaxAge()
        )
                .isEqualTo(
                        Duration.ofHours(1)
                                .getSeconds()
                );
    }

    @Test
    void shouldIgnoreBlankAndNullOrigins() {

        CorsProperties properties =
                validProperties();

        LinkedHashSet<String> origins =
                new LinkedHashSet<>();

        origins.add(
                "http://localhost:4200"
        );
        origins.add(
                ""
        );
        origins.add(
                null
        );

        properties.setAllowedOrigins(
                origins
        );

        CorsConfigurationSource source =
                configuration.corsConfigurationSource(
                        properties
                );

        org.springframework.web.cors.CorsConfiguration resolved =
                source.getCorsConfiguration(
                        new MockHttpServletRequest(
                                "OPTIONS",
                                "/api/v1/me"
                        )
                );

        assertThat(
                resolved
        )
                .isNotNull();

        assertThat(
                resolved.getAllowedOrigins()
        )
                .containsExactly(
                        "http://localhost:4200"
                );
    }

    @Test
    void shouldRejectEmptyAllowedMethods() {

        CorsProperties properties =
                validProperties();

        properties.setAllowedMethods(
                Set.of()
        );

        assertInvalid(
                properties,
                "allowed-methods must not be empty"
        );
    }

    @Test
    void shouldRejectUnsupportedMethod() {

        CorsProperties properties =
                validProperties();

        properties.setAllowedMethods(
                Set.of(
                        "GET",
                        "TRACE",
                        "OPTIONS"
                )
        );

        assertInvalid(
                properties,
                "Unsupported CORS method"
        );
    }

    @Test
    void shouldRejectMethodsWithoutOptions() {

        CorsProperties properties =
                validProperties();

        properties.setAllowedMethods(
                Set.of(
                        "GET",
                        "POST"
                )
        );

        assertInvalid(
                properties,
                "must include OPTIONS"
        );
    }

    @Test
    void shouldRejectEmptyAllowedHeaders() {

        CorsProperties properties =
                validProperties();

        properties.setAllowedHeaders(
                Set.of()
        );

        assertInvalid(
                properties,
                "allowed-headers must not be empty"
        );
    }

    @Test
    void shouldRejectWildcardAllowedHeaders() {

        CorsProperties properties =
                validProperties();

        properties.setAllowedHeaders(
                Set.of(
                        "*"
                )
        );

        assertInvalid(
                properties,
                "allowed-headers must be explicit"
        );
    }

    @Test
    void shouldRejectNullMaxAge() {

        CorsProperties properties =
                validProperties();

        properties.setMaxAge(
                null
        );

        assertInvalid(
                properties,
                "max-age must not be negative"
        );
    }

    @Test
    void shouldRejectNegativeMaxAge() {

        CorsProperties properties =
                validProperties();

        properties.setMaxAge(
                Duration.ofSeconds(
                        -1
                )
        );

        assertInvalid(
                properties,
                "max-age must not be negative"
        );
    }

    @Test
    void shouldRejectWildcardOrigin() {

        assertInvalidOrigin(
                "http://*.example.test",
                "must not contain wildcards"
        );
    }

    @Test
    void shouldRejectRelativeOrigin() {

        assertInvalidOrigin(
                "/control",
                "only scheme, host and optional port"
        );
    }

    @Test
    void shouldRejectUnsupportedOriginScheme() {

        assertInvalidOrigin(
                "ftp://example.test",
                "only scheme, host and optional port"
        );
    }

    @Test
    void shouldRejectOriginWithPath() {

        assertInvalidOrigin(
                "https://example.test/control",
                "only scheme, host and optional port"
        );
    }

    @Test
    void shouldRejectOriginWithQuery() {

        assertInvalidOrigin(
                "https://example.test?source=control",
                "only scheme, host and optional port"
        );
    }

    @Test
    void shouldRejectOriginWithFragment() {

        assertInvalidOrigin(
                "https://example.test#control",
                "only scheme, host and optional port"
        );
    }

    @Test
    void shouldRejectOriginWithUserInfo() {

        assertInvalidOrigin(
                "https://user@example.test",
                "only scheme, host and optional port"
        );
    }

    @Test
    void shouldRejectMalformedOrigin() {

        assertInvalidOrigin(
                "https://example test",
                "Invalid CORS allowed origin"
        );
    }

    private void assertInvalidOrigin(
            String origin,
            String expectedMessage
    ) {

        CorsProperties properties =
                validProperties();

        properties.setAllowedOrigins(
                Set.of(
                        origin
                )
        );

        assertInvalid(
                properties,
                expectedMessage
        );
    }

    private void assertInvalid(
            CorsProperties properties,
            String expectedMessage
    ) {

        assertThatThrownBy(
                () ->
                        configuration
                                .corsConfigurationSource(
                                        properties
                                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        expectedMessage
                );
    }

    private CorsProperties validProperties() {

        CorsProperties properties =
                new CorsProperties();

        properties.setAllowedOrigins(
                Set.of(
                        "http://localhost:4200"
                )
        );

        properties.setAllowedMethods(
                Set.of(
                        "GET",
                        "POST",
                        "OPTIONS"
                )
        );

        properties.setAllowedHeaders(
                Set.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        properties.setAllowCredentials(
                false
        );

        properties.setMaxAge(
                Duration.ofHours(1)
        );

        return properties;
    }
}
