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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(
        OAuth2ClientProperties.class
)
public class RegisteredClientConfiguration {

    private static final Map<String, AuthorizationGrantType>
            SUPPORTED_PUBLIC_GRANT_TYPES =
            Map.of(
                    "authorization_code",
                    AuthorizationGrantType.AUTHORIZATION_CODE,

                    "refresh_token",
                    AuthorizationGrantType.REFRESH_TOKEN
            );

    @Bean
    RegisteredClientRepository registeredClientRepository(
            OAuth2ClientProperties properties
    ) {

        validateGlobalTokenPolicy(
                properties
        );

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

        Set<AuthorizationGrantType> grantTypes =
                resolveGrantTypes(
                        registrationName,
                        client.getGrantTypes()
                );

        Set<String> redirectUris =
                validateUris(
                        registrationName
                                + ".redirect-uris",
                        client.getRedirectUris(),
                        grantTypes.contains(
                                AuthorizationGrantType.AUTHORIZATION_CODE
                        )
                );

        Set<String> postLogoutRedirectUris =
                validateUris(
                        registrationName
                                + ".post-logout-redirect-uris",
                        client.getPostLogoutRedirectUris(),
                        false
                );

        if (client.getScopes().isEmpty()) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + registrationName
                            + ".scopes must contain at least one scope"
            );
        }

        Duration accessTokenTimeToLive =
                resolveDuration(
                        client.getAccessTokenTimeToLive(),
                        properties.getAccessTokenTimeToLive(),
                        registrationName
                                + ".access-token-time-to-live"
                );

        Duration refreshTokenTimeToLive =
                resolveDuration(
                        client.getRefreshTokenTimeToLive(),
                        properties.getRefreshTokenTimeToLive(),
                        registrationName
                                + ".refresh-token-time-to-live"
                );

        TokenSettings tokenSettings =
                TokenSettings.builder()
                        .accessTokenTimeToLive(
                                accessTokenTimeToLive
                        )
                        .refreshTokenTimeToLive(
                                refreshTokenTimeToLive
                        )

                        .reuseRefreshTokens(
                                false
                        )
                        .build();

        ClientSettings clientSettings =
                ClientSettings.builder()
                        .requireProofKey(
                                true
                        )
                        .requireAuthorizationConsent(
                                false
                        )
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
                        .clientSettings(
                                clientSettings
                        )
                        .tokenSettings(
                                tokenSettings
                        );

        grantTypes.forEach(
                builder::authorizationGrantType
        );

        redirectUris.forEach(
                builder::redirectUri
        );

        postLogoutRedirectUris.forEach(
                builder::postLogoutRedirectUri
        );

        client.getScopes()
                .forEach(
                        builder::scope
                );

        return builder.build();
    }

    private Set<AuthorizationGrantType> resolveGrantTypes(
            String registrationName,
            Set<String> configuredGrantTypes
    ) {

        if (configuredGrantTypes.isEmpty()) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + registrationName
                            + ".grant-types must not be empty"
            );
        }

        Set<AuthorizationGrantType> resolved =
                new LinkedHashSet<>();

        for (
                String configuredGrantType
                : configuredGrantTypes
        ) {

            AuthorizationGrantType grantType =
                    SUPPORTED_PUBLIC_GRANT_TYPES.get(
                            configuredGrantType
                    );

            if (grantType == null) {

                throw new IllegalStateException(
                        "Unsupported OAuth2 public-client grant type '"
                                + configuredGrantType
                                + "' for "
                                + registrationName
                );
            }

            resolved.add(
                    grantType
            );
        }

        if (
                resolved.contains(
                        AuthorizationGrantType.REFRESH_TOKEN
                )
                        && !resolved.contains(
                        AuthorizationGrantType.AUTHORIZATION_CODE
                )
        ) {

            throw new IllegalStateException(
                    "OAuth2 public client "
                            + registrationName
                            + " cannot enable refresh_token "
                            + "without authorization_code"
            );
        }

        return resolved;
    }

    private Set<String> validateUris(
            String property,
            Set<String> configuredUris,
            boolean required
    ) {

        if (
                required
                        && configuredUris.isEmpty()
        ) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + property
                            + " must contain at least one URI"
            );
        }

        Set<String> validatedUris =
                new LinkedHashSet<>();

        for (
                String configuredUri
                : configuredUris
        ) {

            requireExactHttpUri(
                    configuredUri,
                    property
            );

            validatedUris.add(
                    configuredUri
            );
        }

        return validatedUris;
    }

    private void requireExactHttpUri(
            String value,
            String property
    ) {

        requireText(
                value,
                property
        );

        if (value.contains("*")) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + property
                            + " must not contain wildcard URIs"
            );
        }

        try {

            URI uri =
                    new URI(
                            value
                    );

            boolean supportedScheme =
                    "http".equalsIgnoreCase(
                            uri.getScheme()
                    )
                            || "https".equalsIgnoreCase(
                            uri.getScheme()
                    );

            if (
                    !uri.isAbsolute()
                            || uri.getHost() == null
                            || !supportedScheme
                            || uri.getFragment() != null
            ) {

                throw new IllegalStateException(
                        "OAuth2 property "
                                + property
                                + " must contain exact absolute "
                                + "HTTP(S) URIs without fragments"
                );
            }
        }
        catch (URISyntaxException exception) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + property
                            + " contains an invalid URI: "
                            + value,
                    exception
            );
        }
    }

    private void validateGlobalTokenPolicy(
            OAuth2ClientProperties properties
    ) {

        requirePositiveDuration(
                properties.getAccessTokenTimeToLive(),
                "access-token-time-to-live"
        );

        requirePositiveDuration(
                properties.getRefreshTokenTimeToLive(),
                "refresh-token-time-to-live"
        );
    }

    private Duration resolveDuration(
            Duration clientValue,
            Duration defaultValue,
            String property
    ) {

        Duration value =
                clientValue == null
                        ? defaultValue
                        : clientValue;

        requirePositiveDuration(
                value,
                property
        );

        return value;
    }

    private void requirePositiveDuration(
            Duration value,
            String property
    ) {

        if (
                value == null
                        || value.isZero()
                        || value.isNegative()
        ) {

            throw new IllegalStateException(
                    "OAuth2 property "
                            + property
                            + " must be a positive duration"
            );
        }
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
