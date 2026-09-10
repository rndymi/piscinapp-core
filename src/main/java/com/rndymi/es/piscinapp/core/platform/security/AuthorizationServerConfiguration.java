package com.rndymi.es.piscinapp.core.platform.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Configuration
public class AuthorizationServerConfiguration {

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            RegisteredClientRepository registeredClientRepository,
            OAuth2TokenGenerator<?> tokenGenerator
    ) {

        http.oauth2AuthorizationServer(
                authorizationServer -> {
                    http.securityMatcher(
                            authorizationServer
                                    .getEndpointsMatcher()
                    );

                    authorizationServer
                            .clientAuthentication(
                                    clientAuthentication ->
                                            clientAuthentication
                                                    .authenticationConverters(
                                                            converters ->
                                                                    converters.add(
                                                                            0,
                                                                            publicClientRefreshAuthenticationConverter()
                                                                    )
                                                    )
                                                    .authenticationProviders(
                                                            providers ->
                                                                    providers.add(
                                                                            0,
                                                                            publicClientRefreshAuthenticationProvider(
                                                                                    registeredClientRepository
                                                                            )
                                                                    )
                                                    )
                            )
                            .tokenGenerator(
                                    tokenGenerator
                            )
                            .oidc(
                                    Customizer.withDefaults()
                            );
                }
        );

        http.cors(
                Customizer.withDefaults()
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
    OAuth2TokenGenerator<?> tokenGenerator(
            JWKSource<SecurityContext> jwkSource,
            OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer
    ) {

        JwtGenerator jwtGenerator =
                new JwtGenerator(
                        new NimbusJwtEncoder(
                                jwkSource
                        )
                );

        jwtGenerator.setJwtCustomizer(
                jwtCustomizer
        );

        OAuth2AccessTokenGenerator accessTokenGenerator =
                new OAuth2AccessTokenGenerator();

        OAuth2TokenGenerator<OAuth2RefreshToken>
                refreshTokenGenerator =
                context -> {

                    if (
                            !OAuth2TokenType.REFRESH_TOKEN.equals(
                                    context.getTokenType()
                            )
                    ) {

                        return null;
                    }

                    byte[] tokenBytes =
                            new byte[96];

                    SECURE_RANDOM.nextBytes(
                            tokenBytes
                    );

                    Instant issuedAt =
                            Instant.now();

                    Instant expiresAt =
                            issuedAt.plus(
                                    context
                                            .getRegisteredClient()
                                            .getTokenSettings()
                                            .getRefreshTokenTimeToLive()
                            );

                    return new OAuth2RefreshToken(
                            Base64
                                    .getUrlEncoder()
                                    .withoutPadding()
                                    .encodeToString(
                                            tokenBytes
                                    ),
                            issuedAt,
                            expiresAt
                    );
                };

        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator,
                accessTokenGenerator,
                refreshTokenGenerator
        );
    }

    private AuthenticationConverter
    publicClientRefreshAuthenticationConverter() {

        return request -> {

            if (!isPublicRefreshRequest(request)) {

                return null;
            }

            String clientId =
                    request.getParameter(
                            OAuth2ParameterNames.CLIENT_ID
                    );

            if (!StringUtils.hasText(clientId)) {

                throw new OAuth2AuthenticationException(
                        OAuth2ErrorCodes.INVALID_REQUEST
                );
            }

            return new OAuth2ClientAuthenticationToken(
                    clientId,
                    ClientAuthenticationMethod.NONE,
                    null,
                    Map.of(
                            OAuth2ParameterNames.GRANT_TYPE,
                            AuthorizationGrantType.REFRESH_TOKEN
                                    .getValue()
                    )
            );
        };
    }

    private AuthenticationProvider
    publicClientRefreshAuthenticationProvider(
            RegisteredClientRepository registeredClientRepository
    ) {

        return new AuthenticationProvider() {

            @Override
            public Authentication authenticate(
                    Authentication authentication
            ) {

                OAuth2ClientAuthenticationToken clientAuthentication =
                        (OAuth2ClientAuthenticationToken) authentication;

                if (
                        !ClientAuthenticationMethod.NONE.equals(
                                clientAuthentication
                                        .getClientAuthenticationMethod()
                        )
                                || !AuthorizationGrantType.REFRESH_TOKEN
                                .getValue()
                                .equals(
                                        clientAuthentication
                                                .getAdditionalParameters()
                                                .get(
                                                        OAuth2ParameterNames.GRANT_TYPE
                                                )
                                )
                ) {

                    return null;
                }

                String clientId =
                        clientAuthentication
                                .getPrincipal()
                                .toString();

                RegisteredClient registeredClient =
                        registeredClientRepository
                                .findByClientId(
                                        clientId
                                );

                if (
                        registeredClient == null
                                || !registeredClient
                                .getClientAuthenticationMethods()
                                .contains(
                                        ClientAuthenticationMethod.NONE
                                )
                                || !registeredClient
                                .getAuthorizationGrantTypes()
                                .contains(
                                        AuthorizationGrantType.REFRESH_TOKEN
                                )
                ) {

                    throw invalidClient();
                }

                return new OAuth2ClientAuthenticationToken(
                        registeredClient,
                        ClientAuthenticationMethod.NONE,
                        null
                );
            }

            @Override
            public boolean supports(
                    Class<?> authentication
            ) {

                return OAuth2ClientAuthenticationToken.class
                        .isAssignableFrom(
                                authentication
                        );
            }
        };
    }

    private boolean isPublicRefreshRequest(
            HttpServletRequest request
    ) {

        if (
                !AuthorizationGrantType.REFRESH_TOKEN
                        .getValue()
                        .equals(
                                request.getParameter(
                                        OAuth2ParameterNames.GRANT_TYPE
                                )
                        )
        ) {

            return false;
        }

        String clientSecret =
                request.getParameter(
                        OAuth2ParameterNames.CLIENT_SECRET
                );

        String authorization =
                request.getHeader(
                        "Authorization"
                );

        return !StringUtils.hasText(clientSecret)
                && (
                !StringUtils.hasText(authorization)
                        || !authorization.startsWith(
                        "Basic "
                )
        );
    }

    private OAuth2AuthenticationException
    invalidClient() {

        return new OAuth2AuthenticationException(
                new OAuth2Error(
                        OAuth2ErrorCodes.INVALID_CLIENT
                )
        );
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
