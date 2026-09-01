package com.rndymi.es.piscinapp.core.pools.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSwimmingPoolStatusRequest(

        @NotNull
        Boolean active
) {
}
