package com.rndymi.es.piscinapp.core.employees.application;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeLookup {

    EmployeeReference requireEmployee(
            UUID employeeId
    );

    Optional<EmployeeReference> findEmployeeByAccountId(
            UUID accountId
    );
}
