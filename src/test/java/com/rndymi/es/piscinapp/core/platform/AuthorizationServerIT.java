package com.rndymi.es.piscinapp.core.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationServerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeOidcProviderConfiguration() throws Exception {
        mockMvc.perform(
                        get("/.well-known/openid-configuration")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.issuer")
                                .value(
                                        "http://localhost:8080"
                                )
                )
                .andExpect(
                        jsonPath("$.authorization_endpoint")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.token_endpoint")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.jwks_uri")
                                .exists()
                );
    }

    @Test
    void shouldExposeOnlyPublicJwkMaterial() throws Exception {
        mockMvc.perform(
                        get("/oauth2/jwks")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.keys")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$.keys[0].kty")
                                .value("RSA")
                )
                .andExpect(
                        jsonPath("$.keys[0].n")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.keys[0].e")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.keys[0].d")
                                .doesNotExist()
                );
    }

    @Test
    void shouldAdvertiseAuthorizationCodeSupport()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/.well-known/openid-configuration"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.response_types_supported"
                        )
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$.grant_types_supported"
                        )
                                .isArray()
                );
    }
}
