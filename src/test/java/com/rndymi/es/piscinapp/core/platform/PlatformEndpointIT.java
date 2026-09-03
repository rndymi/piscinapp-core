package com.rndymi.es.piscinapp.core.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PlatformEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeHealthEndpoint() throws Exception {
        mockMvc.perform(
                        get("/actuator/health")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldExposeOpenApiInTestProfile() throws Exception {
        mockMvc.perform(
                        get("/v3/api-docs")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldExposeSwaggerUiInTestProfile() throws Exception {
        mockMvc.perform(
                        get("/swagger-ui/index.html")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldRejectAnonymousProtectedResourceWithBearerChallenge()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/bootstrap-probe")
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
    void shouldRejectAnonymousAccessToNonPublicActuatorPath()
            throws Exception {

        mockMvc.perform(
                        get("/actuator/env")
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
    void shouldNotExposeEnvActuatorEndpoint()
            throws Exception {

        mockMvc.perform(
                        get("/actuator/env")
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .user("technical-test")
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }
}
