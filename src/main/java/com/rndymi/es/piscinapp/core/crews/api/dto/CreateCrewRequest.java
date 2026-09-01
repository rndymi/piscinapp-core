package com.rndymi.es.piscinapp.core.crews.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCrewRequest(

        @NotBlank
        @Size(max = 150)
        String name
) {
}
