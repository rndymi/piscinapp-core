package com.rndymi.es.piscinapp.core.platform.security;

import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PersistedAccountAuthenticationTests {

    private static final String PASSWORD =
            "test-password-123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository
            userAccountRepository;

    @Autowired
    private UserAccountService
            userAccountService;

    @BeforeEach
    void setUp() {

        userAccountRepository.deleteAll();
    }

    @Test
    void shouldAuthenticateEnabledPersistedAccount()
            throws Exception {

        userAccountService.createAccount(
                "security.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        mockMvc.perform(
                        post("/login")
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .param(
                                        "username",
                                        "security.user"
                                )
                                .param(
                                        "password",
                                        PASSWORD
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl("/")
                );
    }

    @Test
    void shouldRejectWrongPassword()
            throws Exception {

        userAccountService.createAccount(
                "security.user",
                PASSWORD,
                true,
                Set.of(
                        SecurityRole.USER
                )
        );

        mockMvc.perform(
                        post("/login")
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .param(
                                        "username",
                                        "security.user"
                                )
                                .param(
                                        "password",
                                        "wrong-password-123"
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login?error"
                        )
                );
    }

    @Test
    void shouldRejectUnknownAccount()
            throws Exception {

        mockMvc.perform(
                        post("/login")
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .param(
                                        "username",
                                        "unknown.user"
                                )
                                .param(
                                        "password",
                                        PASSWORD
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login?error"
                        )
                );
    }

    @Test
    void shouldRejectDisabledAccount()
            throws Exception {

        userAccountService.createAccount(
                "disabled.user",
                PASSWORD,
                false,
                Set.of(
                        SecurityRole.USER
                )
        );

        mockMvc.perform(
                        post("/login")
                                .with(
                                        SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .param(
                                        "username",
                                        "disabled.user"
                                )
                                .param(
                                        "password",
                                        PASSWORD
                                )
                )
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(
                                "/login?error"
                        )
                );
    }
}
