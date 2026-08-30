package com.rndymi.es.piscinapp.core.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class RegisteredClientConfigurationTests {

    @Autowired
    private RegisteredClientRepository
            registeredClientRepository;

    @Test
    void shouldResolveConfiguredTestClient() {

        RegisteredClient client =
                registeredClientRepository
                        .findByClientId(
                                "piscinapp-test"
                        );

        assertThat(client)
                .isNotNull();

        assertThat(
                client.getClientSecret()
        )
                .isNull();

        assertThat(
                client.getClientAuthenticationMethods()
        )
                .containsExactly(
                        ClientAuthenticationMethod.NONE
                );

        assertThat(
                client.getAuthorizationGrantTypes()
        )
                .containsExactly(
                        AuthorizationGrantType
                                .AUTHORIZATION_CODE
                );

        assertThat(
                client.getRedirectUris()
        )
                .containsExactly(
                        "https://client.example.test/callback"
                );

        assertThat(
                client.getClientSettings()
                        .isRequireProofKey()
        )
                .isTrue();

        assertThat(
                client.getClientSettings()
                        .isRequireAuthorizationConsent()
        )
                .isFalse();
    }

    @Test
    void shouldNotResolveUnknownClient() {

        assertThat(
                registeredClientRepository
                        .findByClientId(
                                "unknown-client"
                        )
        )
                .isNull();
    }
}
