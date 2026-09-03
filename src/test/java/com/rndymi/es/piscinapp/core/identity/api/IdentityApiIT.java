package com.rndymi.es.piscinapp.core.identity.api;

import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class IdentityApiIT {

    private static final String
            PASSWORD =
            "identity-password-123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountService
            userAccountService;

    @Autowired
    private UserAccountRepository
            userAccountRepository;

    @Autowired
    private PasswordEncoder
            passwordEncoder;

    @BeforeEach
    void setUp() {

        userAccountRepository
                .deleteAll();
    }

    @Test
    void shouldReturnCurrentAccount()
            throws Exception {

        UserAccount account =
                userAccountService
                        .createAccount(
                                "current.user",
                                PASSWORD,
                                true,
                                EnumSet.of(
                                        SecurityRole.USER
                                )
                        );

        mockMvc.perform(
                        get(
                                "/api/v1/me"
                        )
                                .with(
                                        jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject(
                                                                        "current.user"
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
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.id"
                        )
                                .value(
                                        account
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.username"
                        )
                                .value(
                                        "current.user"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.enabled"
                        )
                                .value(
                                        true
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.password"
                        )
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath(
                                "$.passwordHash"
                        )
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath(
                                "$.owner"
                        )
                                .value(
                                        false
                                )
                );
    }

    @Test
    void shouldRejectUserAccountAdministration()
            throws Exception {

        userAccountService
                .createAccount(
                        "normal.user",
                        PASSWORD,
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/users"
                        )
                                .with(
                                        jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject(
                                                                        "normal.user"
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
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "ACCESS_DENIED"
                                )
                );
    }

    @Test
    void shouldAllowAdminToCreateAccount()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/users"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "username": " Operator.One ",
                                          "password": "operator-password-123",
                                          "enabled": true,
                                          "roles": [
                                            "USER"
                                          ]
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        header()
                                .string(
                                        "Location",
                                        org.hamcrest.Matchers
                                                .startsWith(
                                                        "/api/v1/users/"
                                                )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.username"
                        )
                                .value(
                                        "operator.one"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.password"
                        )
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath(
                                "$.passwordHash"
                        )
                                .doesNotExist()
                );
    }

    @Test
    void shouldReturnUsernameConflict()
            throws Exception {

        userAccountService
                .createAccount(
                        "duplicate.user",
                        PASSWORD,
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        mockMvc.perform(
                        post(
                                "/api/v1/users"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "username": " Duplicate.User ",
                                          "password": "another-password-123",
                                          "enabled": true,
                                          "roles": [
                                            "USER"
                                          ]
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "IDENTITY_USERNAME_CONFLICT"
                                )
                );
    }

    @Test
    void shouldReturnValidationProblem()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/users"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "username": "",
                                          "password": "short",
                                          "enabled": true,
                                          "roles": []
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.errors"
                        )
                                .isArray()
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAccount()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/users/"
                                        + UUID.randomUUID()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "IDENTITY_USER_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldRejectMalformedAccountId()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/users/not-a-uuid"
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldChangeOwnPassword()
            throws Exception {

        userAccountService
                .createAccount(
                        "password.user",
                        PASSWORD,
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        String newPassword =
                "new-password-value-123";

        mockMvc.perform(
                        put(
                                "/api/v1/me/password"
                        )
                                .with(
                                        jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject(
                                                                        "password.user"
                                                                )
                                                )
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_USER"
                                                        )
                                                )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "currentPassword": "%s",
                                          "newPassword": "%s"
                                        }
                                        """
                                                .formatted(
                                                        PASSWORD,
                                                        newPassword
                                                )
                                )
                )
                .andExpect(
                        status().isNoContent()
                );

        UserAccount account =
                userAccountRepository
                        .findByUsername(
                                "password.user"
                        )
                        .orElseThrow();

        assertThat(
                passwordEncoder.matches(
                        newPassword,
                        account.getPasswordHash()
                )
        )
                .isTrue();
    }

    @Test
    void shouldRejectWrongCurrentPassword()
            throws Exception {

        userAccountService
                .createAccount(
                        "password.user",
                        PASSWORD,
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/me/password"
                        )
                                .with(
                                        jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject(
                                                                        "password.user"
                                                                )
                                                )
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_USER"
                                                        )
                                                )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "currentPassword": "wrong-password-123",
                                          "newPassword": "new-password-value-123"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "IDENTITY_INVALID_CURRENT_PASSWORD"
                                )
                );
    }

    @Test
    void shouldAllowOwnerToChangeOwnPassword()
            throws Exception {

        UserAccount owner =
                createOwner();

        String newPassword =
                "new-owner-password-123";

        mockMvc.perform(
                        put(
                                "/api/v1/me/password"
                        )
                                .with(
                                        jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject(
                                                                        owner.getUsername()
                                                                )
                                                )
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_USER"
                                                        ),
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_ADMIN"
                                                        )
                                                )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "currentPassword": "%s",
                                          "newPassword": "%s"
                                        }
                                        """
                                                .formatted(
                                                        PASSWORD,
                                                        newPassword
                                                )
                                )
                )
                .andExpect(
                        status().isNoContent()
                );

        UserAccount persisted =
                userAccountRepository
                        .findById(
                                owner.getId()
                        )
                        .orElseThrow();

        assertThat(
                passwordEncoder.matches(
                        newPassword,
                        persisted.getPasswordHash()
                )
        )
                .isTrue();
    }

    @Test
    void shouldProtectLastEnabledAdministrator()
            throws Exception {

        UserAccount admin =
                userAccountService
                        .createAccount(
                                "last.admin",
                                PASSWORD,
                                true,
                                EnumSet.of(
                                        SecurityRole.USER,
                                        SecurityRole.ADMIN
                                )
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/users/"
                                        + admin.getId()
                                        + "/status"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "enabled": false
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "IDENTITY_LAST_ADMIN_CONFLICT"
                                )
                );
    }

    @Test
    void shouldAllowAdministratorToDisableAnotherNonOwnerAdministrator()
            throws Exception {

        userAccountService
                .createAccount(
                        "second.admin",
                        PASSWORD,
                        true,
                        EnumSet.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        UserAccount targetAdmin =
                userAccountService
                        .createAccount(
                                "target.admin",
                                PASSWORD,
                                true,
                                EnumSet.of(
                                        SecurityRole.USER,
                                        SecurityRole.ADMIN
                                )
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/users/{id}/status",
                                targetAdmin.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "enabled": false
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.id"
                        )
                                .value(
                                        targetAdmin
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.enabled"
                        )
                                .value(
                                        false
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.owner"
                        )
                                .value(
                                        false
                                )
                );

        UserAccount persisted =
                userAccountRepository
                        .findById(
                                targetAdmin.getId()
                        )
                        .orElseThrow();

        assertThat(
                persisted.isEnabled()
        )
                .isFalse();

        assertThat(
                persisted.isOwner()
        )
                .isFalse();
    }

    @Test
    void shouldRejectOwnerRoleReplacement()
            throws Exception {

        UserAccount owner =
                createOwner();

        mockMvc.perform(
                        put(
                                "/api/v1/users/{id}/roles",
                                owner.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "roles": [
                                            "USER"
                                          ]
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "OWNER_ACCOUNT_PROTECTED"
                                )
                );
    }

    @Test
    void shouldRejectOwnerDisable()
            throws Exception {

        UserAccount owner =
                createOwner();

        mockMvc.perform(
                        put(
                                "/api/v1/users/{id}/status",
                                owner.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "enabled": false
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "OWNER_ACCOUNT_PROTECTED"
                                )
                );
    }

    @Test
    void shouldRejectOwnerPasswordResetByAdministrator()
            throws Exception {

        UserAccount owner =
                createOwner();

        mockMvc.perform(
                        put(
                                "/api/v1/users/{id}/password",
                                owner.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "password": "replacement-password-123"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "OWNER_ACCOUNT_PROTECTED"
                                )
                );
    }

    private org.springframework.test.web.servlet
            .request.RequestPostProcessor
    adminJwt() {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        "api.admin"
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        ),
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                );
    }

    private UserAccount createOwner() {

        return userAccountRepository
                .saveAndFlush(
                        UserAccount.createOwner(
                                UUID.randomUUID(),
                                "protected.owner",
                                passwordEncoder.encode(
                                        PASSWORD
                                )
                        )
                );
    }
}
