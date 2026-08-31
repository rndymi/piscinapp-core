package com.rndymi.es.piscinapp.core.identity.api.dto;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateUserRolesRequest(

        @NotEmpty
        Set<SecurityRole> roles
) {
}
