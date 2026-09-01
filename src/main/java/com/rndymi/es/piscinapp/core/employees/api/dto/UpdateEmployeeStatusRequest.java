package com.rndymi.es.piscinapp.core.employees.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateEmployeeStatusRequest(

        @NotNull
        Boolean active
) {
}
