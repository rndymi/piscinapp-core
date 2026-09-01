package com.rndymi.es.piscinapp.core.maintenance.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateMaintenanceActivityStatusRequest(

        @NotNull
        Boolean active
) {
}
