package com.rndymi.es.piscinapp.core.employees.application;

import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeAccountConflictException;
import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeNotFoundException;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeSpecifications;
import com.rndymi.es.piscinapp.core.identity.application.IdentityAccountLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String
            ACCOUNT_CONSTRAINT =
            "uk_employees_user_account_id";

    private final EmployeeRepository employeeRepository;
    private final IdentityAccountLookup identityAccountLookup;

    @Transactional
    public Employee createEmployee(
            String firstName,
            String familyName
    ) {

        Employee employee =
                new Employee(
                        UUID.randomUUID(),
                        normalizeName(
                                firstName
                        ),
                        normalizeName(
                                familyName
                        )
                );

        return employeeRepository
                .saveAndFlush(
                        employee
                );
    }

    @Transactional(readOnly = true)
    public Employee getEmployee(
            UUID employeeId
    ) {

        return employeeRepository
                .findById(
                        employeeId
                )
                .orElseThrow(
                        () ->
                                new EmployeeNotFoundException(
                                        employeeId
                                )
                );
    }

    @Transactional(readOnly = true)
    public Page<Employee> listEmployees(
            Boolean active,
            String search,
            Pageable pageable
    ) {

        Specification<Employee> specification =
                EmployeeSpecifications
                        .activeEquals(
                                active
                        )
                        .and(
                                EmployeeSpecifications
                                        .nameContains(
                                                search
                                        )
                        );

        return employeeRepository.findAll(
                specification,
                pageable
        );
    }

    @Transactional
    public Employee updateEmployee(
            UUID employeeId,
            String firstName,
            String familyName
    ) {

        Employee employee =
                getEmployeeForUpdate(
                        employeeId
                );

        employee.updateName(
                normalizeName(
                        firstName
                ),
                normalizeName(
                        familyName
                )
        );

        return employee;
    }

    @Transactional
    public Employee updateStatus(
            UUID employeeId,
            boolean active
    ) {

        Employee employee =
                getEmployeeForUpdate(
                        employeeId
                );

        if (active) {

            employee.activate();
        } else {

            employee.deactivate();
        }

        return employee;
    }

    @Transactional
    public Employee associateAccount(
            UUID employeeId,
            UUID userAccountId
    ) {

        Employee employee =
                getEmployeeForUpdate(
                        employeeId
                );

        identityAccountLookup
                .requireExistingAccount(
                        userAccountId
                );

        if (
                employeeRepository
                        .existsByUserAccountIdAndIdNot(
                                userAccountId,
                                employeeId
                        )
        ) {

            throw new EmployeeAccountConflictException(
                    userAccountId
            );
        }

        employee.associateAccount(
                userAccountId
        );

        try {

            return employeeRepository
                    .saveAndFlush(
                            employee
                    );

        } catch (
                DataIntegrityViolationException
                        exception
        ) {

            if (
                    isAccountConstraintViolation(
                            exception
                    )
            ) {

                throw new EmployeeAccountConflictException(
                        userAccountId
                );
            }

            throw exception;
        }
    }

    @Transactional
    public Employee removeAccountAssociation(
            UUID employeeId
    ) {

        Employee employee =
                getEmployeeForUpdate(
                        employeeId
                );

        employee.removeAccountAssociation();

        return employee;
    }

    private Employee getEmployeeForUpdate(
            UUID employeeId
    ) {

        return employeeRepository
                .findById(
                        employeeId
                )
                .orElseThrow(
                        () ->
                                new EmployeeNotFoundException(
                                        employeeId
                                )
                );
    }

    private String normalizeName(
            String value
    ) {

        return value.strip();
    }

    private boolean isAccountConstraintViolation(
            DataIntegrityViolationException exception
    ) {

        Throwable current =
                exception;

        while (current != null) {

            String message =
                    current.getMessage();

            if (
                    message != null
                            &&
                            message.contains(
                                    ACCOUNT_CONSTRAINT
                            )
            ) {

                return true;
            }

            current =
                    current.getCause();
        }

        return false;
    }
}
