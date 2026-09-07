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
class RegisteredClientConfigurationIT {

    private static final String CONTROL_CLIENT_ID = "piscinapp-control-test";

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

    @Test
    void shouldResolveDedicatedControlClient() {

        RegisteredClient client =
                registeredClientRepository
                        .findByClientId(
                                CONTROL_CLIENT_ID
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
                .containsExactlyInAnyOrder(
                        AuthorizationGrantType.AUTHORIZATION_CODE,
                        AuthorizationGrantType.REFRESH_TOKEN
                );

        assertThat(
                client.getScopes()
        )
                .containsExactlyInAnyOrder(
                        "openid",
                        "profile",
                        "offline_access"
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
    void shouldKeepSwaggerStyleTestClientSeparatedFromControl() {

        RegisteredClient testClient =
                registeredClientRepository
                        .findByClientId(
                                "piscinapp-test"
                        );

        RegisteredClient controlClient =
                registeredClientRepository
                        .findByClientId(
                                CONTROL_CLIENT_ID
                        );

        assertThat(testClient)
                .isNotNull();

        assertThat(controlClient)
                .isNotNull();

        assertThat(
                testClient.getId()
        )
                .isNotEqualTo(
                        controlClient.getId()
                );

        assertThat(
                testClient.getAuthorizationGrantTypes()
        )
                .doesNotContain(
                        AuthorizationGrantType.REFRESH_TOKEN
                );

        assertThat(
                controlClient.getAuthorizationGrantTypes()
        )
                .contains(
                        AuthorizationGrantType.REFRESH_TOKEN
                );
    }

    @Test
    void shouldRegisterMultipleControlRedirectUris() {

        RegisteredClient client =
                registeredClientRepository
                        .findByClientId(
                                CONTROL_CLIENT_ID
                        );

        assertThat(
                client.getRedirectUris()
        )
                .containsExactlyInAnyOrder(
                        "https://control.example.test/callback",
                        "https://control-alt.example.test/callback"
                );
    }

    @Test
    void shouldRegisterMultipleControlPostLogoutRedirectUris() {

        RegisteredClient client =
                registeredClientRepository
                        .findByClientId(
                                CONTROL_CLIENT_ID
                        );

        assertThat(
                client.getPostLogoutRedirectUris()
        )
                .containsExactlyInAnyOrder(
                        "https://control.example.test",
                        "https://control-alt.example.test"
                );
    }

    @Test
    void shouldUseBoundedRotatingRefreshTokensForControl() {

        RegisteredClient client =
                registeredClientRepository
                        .findByClientId(
                                CONTROL_CLIENT_ID
                        );

        assertThat(
                client.getTokenSettings()
                        .getRefreshTokenTimeToLive()
        )
                .isEqualTo(
                        java.time.Duration.ofHours(8)
                );

        assertThat(
                client.getTokenSettings()
                        .isReuseRefreshTokens()
        )
                .isFalse();
    }
}
