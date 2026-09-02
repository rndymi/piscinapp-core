package com.rndymi.es.piscinapp.core.identity.api.dto;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;

import java.util.List;
import java.util.UUID;

public record UserAccountResponse(
        UUID id,
        String username,
        boolean enabled,
        List<SecurityRole> roles,
        boolean owner
) {

    public static UserAccountResponse from(
            UserAccount account
    ) {

        return new UserAccountResponse(
                account.getId(),
                account.getUsername(),
                account.isEnabled(),
                account.getRoles()
                        .stream()
                        .sorted()
                        .toList(),
                account.isOwner()
        );
    }
}
