package com.rndymi.es.piscinapp.core.incidents.api;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class IncidentSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void shouldRequireAuthenticationForIncidentReads()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/incidents/{incidentId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldKeepAdministrativeSearchAdminOnly()
            throws Exception {

        String username =
                "regular-user";

        userAccountRepository.saveAndFlush(
                new UserAccount(
                        UUID.randomUUID(),
                        username,
                        "{noop}unused-password",
                        true,
                        Set.of(
                                SecurityRole.USER
                        )
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/incidents"
                        )
                                .with(
                                        jwt()
                                                .jwt(
                                                        token ->
                                                                token.subject(
                                                                        username
                                                                )
                                                )
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_USER"
                                                        )
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }
}
