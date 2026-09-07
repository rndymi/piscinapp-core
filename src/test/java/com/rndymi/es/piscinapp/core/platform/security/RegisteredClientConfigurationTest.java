package com.rndymi.es.piscinapp.core.platform.security;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisteredClientConfigurationTest {

    private final RegisteredClientConfiguration configuration =
            new RegisteredClientConfiguration();

    @Test
    void shouldRejectWildcardRedirectUri() {

        OAuth2ClientProperties properties =
                baseProperties();

        properties.getClients()
                .get("control")
                .setRedirectUris(
                        Set.of(
                                "http://localhost:4200/*"
                        )
                );

        assertThatThrownBy(
                () ->
                        configuration
                                .registeredClientRepository(
                                        properties
                                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "wildcard"
                );
    }

    @Test
    void shouldRejectWildcardPostLogoutRedirectUri() {

        OAuth2ClientProperties properties =
                baseProperties();

        properties.getClients()
                .get("control")
                .setPostLogoutRedirectUris(
                        Set.of(
                                "http://localhost:4200/*"
                        )
                );

        assertThatThrownBy(
                () ->
                        configuration
                                .registeredClientRepository(
                                        properties
                                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "wildcard"
                );
    }

    @Test
    void shouldRejectUnknownPublicGrantType() {

        OAuth2ClientProperties properties =
                baseProperties();

        properties.getClients()
                .get("control")
                .setGrantTypes(
                        Set.of(
                                "authorization_code",
                                "client_credentials"
                        )
                );

        assertThatThrownBy(
                () ->
                        configuration
                                .registeredClientRepository(
                                        properties
                                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessageContaining(
                        "Unsupported OAuth2 public-client grant type"
                );
    }

    private OAuth2ClientProperties baseProperties() {

        OAuth2ClientProperties properties =
                new OAuth2ClientProperties();

        OAuth2ClientProperties.Client client =
                new OAuth2ClientProperties.Client();

        client.setEnabled(
                true
        );

        client.setClientId(
                "piscinapp-control"
        );

        client.setGrantTypes(
                Set.of(
                        "authorization_code",
                        "refresh_token"
                )
        );

        client.setRedirectUris(
                Set.of(
                        "http://localhost:4200/callback"
                )
        );

        client.setPostLogoutRedirectUris(
                Set.of(
                        "http://localhost:4200"
                )
        );

        client.setScopes(
                Set.of(
                        "openid",
                        "profile",
                        "offline_access"
                )
        );

        properties.getClients()
                .put(
                        "control",
                        client
                );

        return properties;
    }
}
