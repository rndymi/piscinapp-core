package com.rndymi.es.piscinapp.core.employees.application;

import java.util.UUID;

public record EmployeeReference(
        UUID id,
        boolean active
) {
}
