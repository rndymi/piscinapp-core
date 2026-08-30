package com.rndymi.es.piscinapp.core.platform.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(
        OAuth2ClientProperties.class
)
public class RegisteredClientConfiguration {

    @Bean
    RegisteredClientRepository registeredClientRepository(
            OAuth2ClientProperties properties
    ) {

        List<RegisteredClient> clients =
                new ArrayList<>();

        for (
                Map.Entry<
                        String,
                        OAuth2ClientProperties.Client
                        > entry
                : properties.getClients().entrySet()
        ) {

            if (!entry.getValue().isEnabled()) {
                continue;
            }

            clients.add(
                    buildPublicClient(
                            entry.getKey(),
                            entry.getValue(),
                            properties
                    )
            );
        }

        return new ConfiguredRegisteredClientRepository(
                clients
        );
    }

    private RegisteredClient buildPublicClient(
            String registrationName,
            OAuth2ClientProperties.Client client,
            OAuth2ClientProperties properties
    ) {

        requireText(
                client.getClientId(),
                registrationName
                        + ".client-id"
        );

        requireText(
                client.getRedirectUri(),
                registrationName
                        + ".redirect-uri"
        );

        if (
                client.getRedirectUri()
                        .contains("*")
        ) {

            throw new IllegalStateException(
                    "OAuth2 redirect URI must not contain wildcards"
            );
        }

        TokenSettings tokenSettings =
                TokenSettings.builder()
                        .accessTokenTimeToLive(
                                properties
                                        .getAccessTokenTimeToLive()
                        )
                        .build();

        ClientSettings clientSettings =
                ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(false)
                        .build();

        RegisteredClient.Builder builder =
                RegisteredClient
                        .withId(
                                "piscinapp-"
                                        + registrationName
                        )
                        .clientId(
                                client.getClientId()
                        )
                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.NONE
                        )
                        .authorizationGrantType(
                                AuthorizationGrantType
                                        .AUTHORIZATION_CODE
                        )
                        .redirectUri(
                                client.getRedirectUri()
                        )
                        .clientSettings(
                                clientSettings
                        )
                        .tokenSettings(
                                tokenSettings
                        );

        client.getScopes()
                .forEach(
                        builder::scope
                );

        return builder.build();
    }

    private void requireText(
            String value,
            String property
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + property
                            + " must be configured"
            );
        }
    }
}
