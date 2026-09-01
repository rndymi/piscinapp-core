package com.rndymi.es.piscinapp.core.maintenance.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMaintenanceActivityRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 1000)
        String description
) {
}
