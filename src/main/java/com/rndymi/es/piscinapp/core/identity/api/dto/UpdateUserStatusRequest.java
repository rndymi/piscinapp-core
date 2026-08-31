package com.rndymi.es.piscinapp.core.identity.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(

        @NotNull
        Boolean enabled
) {
}
