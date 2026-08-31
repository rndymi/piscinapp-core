package com.rndymi.es.piscinapp.core.identity.api.dto;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserAccountRequest(

        @NotBlank
        @Size(
                max =
                        UserAccount
                                .USERNAME_MAX_LENGTH
        )
        String username,

        @NotBlank
        @Size(
                min = 12,
                max = 128
        )
        String password,

        @NotNull
        Boolean enabled,

        @NotEmpty
        Set<SecurityRole> roles
) {
}
