package com.rndymi.es.piscinapp.core.execution.application;

import com.rndymi.es.piscinapp.core.employees.application.EmployeeLookup;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeReference;
import com.rndymi.es.piscinapp.core.execution.application.exception.VisitExecutionForbiddenException;
import com.rndymi.es.piscinapp.core.identity.application.IdentityAccountLookup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationalActorResolverTest {

    @Mock
    private IdentityAccountLookup identityAccountLookup;

    @Mock
    private EmployeeLookup employeeLookup;

    @InjectMocks
    private OperationalActorResolver resolver;

    @Test
    void shouldResolveActiveEmployeeFromPrincipal() {

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "worker"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.of(
                                new EmployeeReference(
                                        employeeId,
                                        true
                                )
                        )
                );

        OperationalActor actor =
                resolver.resolve(
                        "worker"
                );

        assertThat(
                actor.accountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                actor.employeeId()
        )
                .isEqualTo(
                        employeeId
                );
    }

    @Test
    void shouldRejectAccountWithoutEmployeeAssociation() {

        UUID accountId =
                UUID.randomUUID();

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "admin"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.empty()
                );

        assertThatThrownBy(
                () ->
                        resolver.resolve(
                                "admin"
                        )
        )
                .isInstanceOf(
                        VisitExecutionForbiddenException.class
                );
    }

    @Test
    void shouldRejectInactiveEmployee() {

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "worker"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.of(
                                new EmployeeReference(
                                        employeeId,
                                        false
                                )
                        )
                );

        assertThatThrownBy(
                () ->
                        resolver.resolve(
                                "worker"
                        )
        )
                .isInstanceOf(
                        VisitExecutionForbiddenException.class
                );
    }
}
