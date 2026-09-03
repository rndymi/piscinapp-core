package com.rndymi.es.piscinapp.core.employees.application;

import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeAccountConflictException;
import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeNotFoundException;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.identity.application.IdentityAccountLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository
            employeeRepository;

    @Mock
    private IdentityAccountLookup
            identityAccountLookup;

    private EmployeeService
            employeeService;

    @BeforeEach
    void setUp() {

        employeeService =
                new EmployeeService(
                        employeeRepository,
                        identityAccountLookup
                );
    }

    @Test
    void shouldNormalizeEmployeeNames() {

        when(
                employeeRepository
                        .saveAndFlush(
                                org.mockito.ArgumentMatchers
                                        .any(
                                                Employee.class
                                        )
                        )
        )
                .thenAnswer(
                        invocation ->
                                invocation
                                        .getArgument(
                                                0
                                        )
                );

        Employee employee =
                employeeService
                        .createEmployee(
                                "  María José  ",
                                "  Pérez Gómez  "
                        );

        assertThat(
                employee.getFirstName()
        )
                .isEqualTo(
                        "María José"
                );

        assertThat(
                employee.getFamilyName()
        )
                .isEqualTo(
                        "Pérez Gómez"
                );

        assertThat(
                employee.isActive()
        )
                .isTrue();
    }

    @Test
    void shouldFailWhenEmployeeDoesNotExist() {

        UUID employeeId =
                UUID.randomUUID();

        when(
                employeeRepository
                        .findById(
                                employeeId
                        )
        )
                .thenReturn(
                        Optional.empty()
                );

        assertThatThrownBy(
                () ->
                        employeeService
                                .getEmployee(
                                        employeeId
                                )
        )
                .isInstanceOf(
                        EmployeeNotFoundException.class
                );
    }

    @Test
    void shouldRequireExistingAccountBeforeAssociation() {

        Employee employee =
                employee();

        UUID accountId =
                UUID.randomUUID();

        when(
                employeeRepository
                        .findById(
                                employee.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                employee
                        )
                );

        when(
                employeeRepository
                        .existsByUserAccountIdAndIdNot(
                                accountId,
                                employee.getId()
                        )
        )
                .thenReturn(
                        false
                );

        when(
                employeeRepository
                        .saveAndFlush(
                                employee
                        )
        )
                .thenReturn(
                        employee
                );

        employeeService
                .associateAccount(
                        employee.getId(),
                        accountId
                );

        verify(
                identityAccountLookup
        )
                .requireExistingAccount(
                        accountId
                );

        assertThat(
                employee.getUserAccountId()
        )
                .isEqualTo(
                        accountId
                );
    }

    @Test
    void shouldRejectAccountAlreadyAssociatedWithAnotherEmployee() {

        Employee employee =
                employee();

        UUID accountId =
                UUID.randomUUID();

        when(
                employeeRepository
                        .findById(
                                employee.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                employee
                        )
                );

        when(
                employeeRepository
                        .existsByUserAccountIdAndIdNot(
                                accountId,
                                employee.getId()
                        )
        )
                .thenReturn(
                        true
                );

        UUID employeeId =
                employee.getId();

        assertThatThrownBy(
                () ->
                        employeeService
                                .associateAccount(
                                        employeeId,
                                        accountId
                                )
        )
                .isInstanceOf(
                        EmployeeAccountConflictException.class
                );
    }

    @Test
    void shouldRemoveAccountWithoutDeletingEmployee() {

        Employee employee =
                employee();

        employee.associateAccount(
                UUID.randomUUID()
        );

        when(
                employeeRepository
                        .findById(
                                employee.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                employee
                        )
                );

        Employee result =
                employeeService
                        .removeAccountAssociation(
                                employee.getId()
                        );

        assertThat(
                result.getUserAccountId()
        )
                .isNull();
    }

    private Employee employee() {

        return new Employee(
                UUID.randomUUID(),
                "Ana",
                "Torres"
        );
    }
}
