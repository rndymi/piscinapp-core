package com.rndymi.es.piscinapp.core.maintenance.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMaintenanceActivityRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 1000)
        String description
) {
}
