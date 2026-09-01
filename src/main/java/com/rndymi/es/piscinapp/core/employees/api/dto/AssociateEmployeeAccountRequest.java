package com.rndymi.es.piscinapp.core.employees.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssociateEmployeeAccountRequest(

        @NotNull
        UUID userAccountId
) {
}
