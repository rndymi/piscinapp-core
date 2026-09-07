package com.rndymi.es.piscinapp.core.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CorsSecurityIT {

    private static final String ALLOWED_ORIGIN =
            "https://control.example.test";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowResourceServerPreflight()
            throws Exception {

        mockMvc.perform(
                        options(
                                "/api/v1/me"
                        )
                                .header(
                                        "Origin",
                                        ALLOWED_ORIGIN
                                )
                                .header(
                                        "Access-Control-Request-Method",
                                        "GET"
                                )
                                .header(
                                        "Access-Control-Request-Headers",
                                        "Authorization"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        header().string(
                                "Access-Control-Allow-Origin",
                                ALLOWED_ORIGIN
                        )
                )
                .andExpect(
                        header().string(
                                "Access-Control-Allow-Credentials",
                                org.hamcrest.Matchers.nullValue()
                        )
                );
    }

    @Test
    void shouldAllowAuthorizationServerTokenPreflight()
            throws Exception {

        mockMvc.perform(
                        options(
                                "/oauth2/token"
                        )
                                .header(
                                        "Origin",
                                        ALLOWED_ORIGIN
                                )
                                .header(
                                        "Access-Control-Request-Method",
                                        "POST"
                                )
                                .header(
                                        "Access-Control-Request-Headers",
                                        "Content-Type"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        header().string(
                                "Access-Control-Allow-Origin",
                                ALLOWED_ORIGIN
                        )
                );
    }

    @Test
    void shouldRejectUnknownOrigin()
            throws Exception {

        mockMvc.perform(
                        options(
                                "/api/v1/me"
                        )
                                .header(
                                        "Origin",
                                        "https://evil.example.test"
                                )
                                .header(
                                        "Access-Control-Request-Method",
                                        "GET"
                                )
                                .header(
                                        "Access-Control-Request-Headers",
                                        "Authorization"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }
}
