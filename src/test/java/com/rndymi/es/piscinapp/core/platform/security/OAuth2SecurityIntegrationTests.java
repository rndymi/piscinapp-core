package com.rndymi.es.piscinapp.core.platform.security;

import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OAuth2SecurityIntegrationTests {

    private static final String CLIENT_ID =
            "piscinapp-test";

    private static final String REDIRECT_URI =
            "https://client.example.test/callback";

    private static final String PASSWORD =
            "oauth2-password-123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService
            userAccountService;

    @Autowired
    private UserAccountRepository
            userAccountRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JWKSource<SecurityContext>
            jwkSource;

    @BeforeEach
    void setUp() {

        userAccountRepository.deleteAll();
    }

    @Test
    void shouldIssueUserTokenWithPersistedRoles()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        String accessToken =
                obtainAccessToken(
                        "oauth.user"
                );

        Jwt jwt =
                jwtDecoder.decode(
                        accessToken
                );

        assertThat(
                jwt.getIssuer()
                        .toString()
        )
                .isEqualTo(
                        "http://localhost:8080"
                );

        assertThat(
                jwt.getClaimAsStringList(
                        "roles"
                )
        )
                .containsExactly(
                        "USER"
                );

        assertThat(
                jwt.getClaims()
        )
                .doesNotContainKeys(
                        "password",
                        "passwordHash",
                        "password_hash"
                );
    }

    @Test
    void shouldIssueAdminTokenWithUserAndAdminRoles()
            throws Exception {

        userAccountService.createAccount(
                "oauth.admin",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                )
        );

        String accessToken =
                obtainAccessToken(
                        "oauth.admin"
                );

        Jwt jwt =
                jwtDecoder.decode(
                        accessToken
                );

        assertThat(
                jwt.getClaimAsStringList(
                        "roles"
                )
        )
                .containsExactlyInAnyOrder(
                        "USER",
                        "ADMIN"
                );
    }

    @Test
    void shouldApplyRoleAuthorizationUsingRealAccessToken()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        String accessToken =
                obtainAccessToken(
                        "oauth.user"
                );

        mockMvc.perform(
                        get(
                                "/api/security-test/user"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                )
                .andExpect(
                        status().isOk()
                );

        mockMvc.perform(
                        get(
                                "/api/security-test/admin"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowAdminUsingRealAccessToken()
            throws Exception {

        userAccountService.createAccount(
                "oauth.admin",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                )
        );

        String accessToken =
                obtainAccessToken(
                        "oauth.admin"
                );

        mockMvc.perform(
                        get(
                                "/api/security-test/admin"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer "
                                                + accessToken
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldRejectAnonymousResourceRequest()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/security-test/user"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        header().string(
                                "WWW-Authenticate",
                                startsWith("Bearer")
                        )
                );
    }

    @Test
    void shouldRejectInvalidSignature()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        String accessToken =
                obtainAccessToken(
                        "oauth.user"
                );

        String tamperedToken =
                tamperJwtSignature(
                        accessToken
                );

        mockMvc.perform(
                        get(
                                "/api/security-test/user"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer "
                                                + tamperedToken
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldRejectWrongCodeVerifier()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        String verifier =
                "correct-test-code-verifier-12345678901234567890";

        String code =
                obtainAuthorizationCode(
                        "oauth.user",
                        verifier
                );

        mockMvc.perform(
                        post("/oauth2/token")
                                .contentType(
                                        MediaType
                                                .APPLICATION_FORM_URLENCODED
                                )
                                .param(
                                        "grant_type",
                                        "authorization_code"
                                )
                                .param(
                                        "client_id",
                                        CLIENT_ID
                                )
                                .param(
                                        "code",
                                        code
                                )
                                .param(
                                        "redirect_uri",
                                        REDIRECT_URI
                                )
                                .param(
                                        "code_verifier",
                                        "incorrect-test-code-verifier-123456789012345"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectAuthorizationRequestWithoutPkce()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        MockHttpSession session =
                login(
                        "oauth.user"
                );

        mockMvc.perform(
                        get("/oauth2/authorize")
                                .session(session)
                                .queryParam(
                                        "response_type",
                                        "code"
                                )
                                .queryParam(
                                        "client_id",
                                        CLIENT_ID
                                )
                                .queryParam(
                                        "redirect_uri",
                                        REDIRECT_URI
                                )
                                .queryParam(
                                        "scope",
                                        "openid profile"
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        header().string(
                                "Location",
                                org.hamcrest.Matchers.allOf(
                                        startsWith(
                                                REDIRECT_URI
                                        ),
                                        org.hamcrest.Matchers.containsString(
                                                "error=invalid_request"
                                        ),
                                        org.hamcrest.Matchers.containsString(
                                                "code_challenge"
                                        )
                                )
                        )
                );
    }

    @Test
    void shouldRejectInvalidRedirectUri()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        MockHttpSession session =
                login(
                        "oauth.user"
                );

        String verifier =
                "piscinapp-invalid-redirect-verifier-12345678901234567890";

        String challenge =
                createCodeChallenge(
                        verifier
                );

        mockMvc.perform(
                        get("/oauth2/authorize")
                                .session(session)
                                .queryParam(
                                        "response_type",
                                        "code"
                                )
                                .queryParam(
                                        "client_id",
                                        CLIENT_ID
                                )
                                .queryParam(
                                        "redirect_uri",
                                        "https://invalid.example.test/callback"
                                )
                                .queryParam(
                                        "scope",
                                        "openid profile"
                                )
                                .queryParam(
                                        "code_challenge",
                                        challenge
                                )
                                .queryParam(
                                        "code_challenge_method",
                                        "S256"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectUnknownClientAtProtocolLevel()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        MockHttpSession session =
                login(
                        "oauth.user"
                );

        String verifier =
                "piscinapp-unknown-client-verifier-12345678901234567890";

        String challenge =
                createCodeChallenge(
                        verifier
                );

        mockMvc.perform(
                        get("/oauth2/authorize")
                                .session(session)
                                .queryParam(
                                        "response_type",
                                        "code"
                                )
                                .queryParam(
                                        "client_id",
                                        "unknown-client"
                                )
                                .queryParam(
                                        "redirect_uri",
                                        REDIRECT_URI
                                )
                                .queryParam(
                                        "scope",
                                        "openid profile"
                                )
                                .queryParam(
                                        "code_challenge",
                                        challenge
                                )
                                .queryParam(
                                        "code_challenge_method",
                                        "S256"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectAuthorizationCodeReuse()
            throws Exception {

        userAccountService.createAccount(
                "oauth.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        String verifier =
                "piscinapp-code-reuse-verifier-123456789012345678901234567890";

        String code =
                obtainAuthorizationCode(
                        "oauth.user",
                        verifier
                );

        mockMvc.perform(
                        post("/oauth2/token")
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED
                                )
                                .param(
                                        "grant_type",
                                        "authorization_code"
                                )
                                .param(
                                        "client_id",
                                        CLIENT_ID
                                )
                                .param(
                                        "code",
                                        code
                                )
                                .param(
                                        "redirect_uri",
                                        REDIRECT_URI
                                )
                                .param(
                                        "code_verifier",
                                        verifier
                                )
                )
                .andExpect(
                        status().isOk()
                );

        mockMvc.perform(
                        post("/oauth2/token")
                                .contentType(
                                        MediaType.APPLICATION_FORM_URLENCODED
                                )
                                .param(
                                        "grant_type",
                                        "authorization_code"
                                )
                                .param(
                                        "client_id",
                                        CLIENT_ID
                                )
                                .param(
                                        "code",
                                        code
                                )
                                .param(
                                        "redirect_uri",
                                        REDIRECT_URI
                                )
                                .param(
                                        "code_verifier",
                                        verifier
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldRejectExpiredToken()
            throws Exception {

        Instant now =
                Instant.now();

        String expiredToken =
                createSignedAccessToken(
                        "http://localhost:8080",
                        now.minusSeconds(
                                1200
                        ),
                        now.minusSeconds(
                                300
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/security-test/user"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer "
                                                + expiredToken
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldRejectTokenWithIncorrectIssuer()
            throws Exception {

        Instant now =
                Instant.now();

        String token =
                createSignedAccessToken(
                        "https://wrong-issuer.example.test",
                        now,
                        now.plusSeconds(
                                900
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/security-test/user"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer "
                                                + token
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    private String obtainAccessToken(
            String username
    )
            throws Exception {

        String verifier =
                "piscinapp-test-code-verifier-123456789012345678901234567890";

        String code =
                obtainAuthorizationCode(
                        username,
                        verifier
                );

        MvcResult result =
                mockMvc.perform(
                                post("/oauth2/token")
                                        .contentType(
                                                MediaType
                                                        .APPLICATION_FORM_URLENCODED
                                        )
                                        .param(
                                                "grant_type",
                                                "authorization_code"
                                        )
                                        .param(
                                                "client_id",
                                                CLIENT_ID
                                        )
                                        .param(
                                                "code",
                                                code
                                        )
                                        .param(
                                                "redirect_uri",
                                                REDIRECT_URI
                                        )
                                        .param(
                                                "code_verifier",
                                                verifier
                                        )
                        )
                        .andExpect(
                                status().isOk()
                        )
                        .andReturn();

        return JsonPath.read(
                result.getResponse()
                        .getContentAsString(),
                "$.access_token"
        );
    }

    private String obtainAuthorizationCode(
            String username,
            String verifier
    )
            throws Exception {

        MockHttpSession session =
                login(username);

        String challenge =
                createCodeChallenge(
                        verifier
                );

        MvcResult result =
                mockMvc.perform(
                                get("/oauth2/authorize")
                                        .session(session)
                                        .queryParam(
                                                "response_type",
                                                "code"
                                        )
                                        .queryParam(
                                                "client_id",
                                                CLIENT_ID
                                        )
                                        .queryParam(
                                                "redirect_uri",
                                                REDIRECT_URI
                                        )
                                        .queryParam(
                                                "scope",
                                                "openid profile"
                                        )
                                        .queryParam(
                                                "code_challenge",
                                                challenge
                                        )
                                        .queryParam(
                                                "code_challenge_method",
                                                "S256"
                                        )
                        )
                        .andExpect(
                                status().is3xxRedirection()
                        )
                        .andReturn();

        String location =
                result.getResponse()
                        .getHeader(
                                "Location"
                        );

        assertThat(location)
                .isNotNull();

        String code =
                UriComponentsBuilder
                        .fromUriString(
                                location
                        )
                        .build()
                        .getQueryParams()
                        .getFirst(
                                "code"
                        );

        assertThat(code)
                .isNotBlank();

        return code;
    }

    private MockHttpSession login(
            String username
    )
            throws Exception {

        MvcResult result =
                mockMvc.perform(
                                post("/login")
                                        .with(
                                                SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .param(
                                                "username",
                                                username
                                        )
                                        .param(
                                                "password",
                                                PASSWORD
                                        )
                        )
                        .andExpect(
                                status().is3xxRedirection()
                        )
                        .andReturn();

        return (
                MockHttpSession
                ) result
                .getRequest()
                .getSession(false);
    }

    private String createCodeChallenge(
            String verifier
    )
            throws Exception {

        byte[] digest =
                MessageDigest
                        .getInstance("SHA-256")
                        .digest(
                                verifier.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        );

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        digest
                );
    }

    private String createSignedAccessToken(
            String issuer,
            Instant issuedAt,
            Instant expiresAt
    ) {

        NimbusJwtEncoder jwtEncoder =
                new NimbusJwtEncoder(
                        jwkSource
                );

        JwsHeader jwsHeader =
                JwsHeader
                        .with(
                                SignatureAlgorithm.RS256
                        )
                        .build();

        JwtClaimsSet claims =
                JwtClaimsSet
                        .builder()
                        .issuer(
                                issuer
                        )
                        .subject(
                                "oauth.test"
                        )
                        .issuedAt(
                                issuedAt
                        )
                        .expiresAt(
                                expiresAt
                        )
                        .claim(
                                "roles",
                                List.of(
                                        "USER"
                                )
                        )
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                jwsHeader,
                                claims
                        )
                )
                .getTokenValue();
    }

    private String tamperJwtSignature(
            String token
    ) {

        String[] parts =
                token.split("\\.");

        if (parts.length != 3) {

            throw new IllegalArgumentException(
                    "Expected a signed JWT"
            );
        }

        String signature =
                parts[2];

        int position =
                signature.length() / 2;

        char original =
                signature.charAt(position);

        char replacement =
                original == 'A'
                        ? 'B'
                        : 'A';

        String tamperedSignature =
                signature.substring(
                        0,
                        position
                )
                        + replacement
                        + signature.substring(
                        position + 1
                );

        return parts[0]
                + "."
                + parts[1]
                + "."
                + tamperedSignature;
    }
}
