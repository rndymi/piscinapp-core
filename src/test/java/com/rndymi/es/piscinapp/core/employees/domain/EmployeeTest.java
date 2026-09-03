package com.rndymi.es.piscinapp.core.employees.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeTest {

    @Test
    void shouldCreateActiveEmployee() {

        Employee employee =
                new Employee(
                        UUID.randomUUID(),
                        "Ana",
                        "Torres Ruiz"
                );

        assertThat(
                employee.isActive()
        )
                .isTrue();

        assertThat(
                employee.getUserAccountId()
        )
                .isNull();

        assertThat(
                employee.getDisplayName()
        )
                .isEqualTo(
                        "Ana Torres Ruiz"
                );
    }

    @Test
    void shouldManageEmployeeLifecycle() {

        Employee employee =
                employee();

        employee.deactivate();

        assertThat(
                employee.isActive()
        )
                .isFalse();

        employee.activate();

        assertThat(
                employee.isActive()
        )
                .isTrue();
    }

    @Test
    void shouldManageAccountAssociation() {

        Employee employee =
                employee();

        UUID accountId =
                UUID.randomUUID();

        employee.associateAccount(
                accountId
        );

        assertThat(
                employee.getUserAccountId()
        )
                .isEqualTo(
                        accountId
                );

        employee.removeAccountAssociation();

        assertThat(
                employee.getUserAccountId()
        )
                .isNull();
    }

    @Test
    void shouldUpdateEmployeeName() {

        Employee employee =
                employee();

        employee.updateName(
                "María José",
                "Pérez Gómez"
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
                employee.getDisplayName()
        )
                .isEqualTo(
                        "María José Pérez Gómez"
                );
    }

    private Employee employee() {

        return new Employee(
                UUID.randomUUID(),
                "Ana",
                "Torres"
        );
    }
}
