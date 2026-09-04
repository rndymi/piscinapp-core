package com.rndymi.es.piscinapp.core.execution.application;

import com.rndymi.es.piscinapp.core.employees.application.EmployeeLookup;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeReference;
import com.rndymi.es.piscinapp.core.execution.application.exception.VisitExecutionForbiddenException;
import com.rndymi.es.piscinapp.core.identity.application.IdentityAccountLookup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OperationalActorResolver {

    private final IdentityAccountLookup identityAccountLookup;
    private final EmployeeLookup employeeLookup;

    public OperationalActor resolve(
            String principalName
    ) {

        UUID accountId =
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                principalName
                        );

        EmployeeReference employee =
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
                        .filter(
                                EmployeeReference::active
                        )
                        .orElseThrow(
                                VisitExecutionForbiddenException::new
                        );

        return new OperationalActor(
                accountId,
                employee.id()
        );
    }
}
