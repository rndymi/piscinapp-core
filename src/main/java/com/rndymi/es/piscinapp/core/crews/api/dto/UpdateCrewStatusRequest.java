package com.rndymi.es.piscinapp.core.crews.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCrewStatusRequest(

        @NotNull
        Boolean active
) {
}
