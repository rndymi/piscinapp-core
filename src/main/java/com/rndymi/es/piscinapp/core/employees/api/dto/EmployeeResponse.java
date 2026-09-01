package com.rndymi.es.piscinapp.core.employees.api.dto;

import com.rndymi.es.piscinapp.core.employees.domain.Employee;

import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String firstName,
        String familyName,
        String displayName,
        boolean active,
        UUID userAccountId
) {

    public static EmployeeResponse from(
            Employee employee
    ) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getFamilyName(),
                employee.getDisplayName(),
                employee.isActive(),
                employee.getUserAccountId()
        );
    }
}
