package com.rndymi.es.piscinapp.core.employees.application;

import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeNotFoundException;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeLookupService
        implements EmployeeLookup {

    private final EmployeeRepository
            employeeRepository;

    public EmployeeLookupService(
            EmployeeRepository employeeRepository
    ) {

        this.employeeRepository =
                employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeReference requireEmployee(
            UUID employeeId
    ) {

        Employee employee =
                employeeRepository
                        .findById(
                                employeeId
                        )
                        .orElseThrow(
                                () ->
                                        new EmployeeNotFoundException(
                                                employeeId
                                        )
                        );

        return new EmployeeReference(
                employee.getId(),
                employee.isActive()
        );
    }
}
