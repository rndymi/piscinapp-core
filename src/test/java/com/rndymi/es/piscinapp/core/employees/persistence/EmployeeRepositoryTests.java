package com.rndymi.es.piscinapp.core.employees.persistence;

import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository
            employeeRepository;

    @Test
    void shouldPersistEmployee() {

        Employee employee =
                new Employee(
                        UUID.randomUUID(),
                        "Ana",
                        "Torres"
                );

        Employee saved =
                employeeRepository
                        .saveAndFlush(
                                employee
                        );

        assertThat(
                employeeRepository
                        .findById(
                                saved.getId()
                        )
        )
                .isPresent()
                .get()
                .satisfies(
                        persisted -> {

                            assertThat(
                                    persisted.getFirstName()
                            )
                                    .isEqualTo(
                                            "Ana"
                                    );

                            assertThat(
                                    persisted.getFamilyName()
                            )
                                    .isEqualTo(
                                            "Torres"
                                    );

                            assertThat(
                                    persisted.isActive()
                            )
                                    .isTrue();

                            assertThat(
                                    persisted.getUserAccountId()
                            )
                                    .isNull();
                        }
                );
    }

    @Test
    void shouldAllowMultipleEmployeesWithoutAccount() {

        Employee first =
                new Employee(
                        UUID.randomUUID(),
                        "Ana",
                        "Torres"
                );

        Employee second =
                new Employee(
                        UUID.randomUUID(),
                        "Luis",
                        "García"
                );

        Employee savedFirst =
                employeeRepository
                        .saveAndFlush(
                                first
                        );

        Employee savedSecond =
                employeeRepository
                        .saveAndFlush(
                                second
                        );

        assertThat(
                savedFirst.getUserAccountId()
        )
                .isNull();

        assertThat(
                savedSecond.getUserAccountId()
        )
                .isNull();

        assertThat(
                employeeRepository
                        .findById(
                                savedFirst.getId()
                        )
        )
                .isPresent();

        assertThat(
                employeeRepository
                        .findById(
                                savedSecond.getId()
                        )
        )
                .isPresent();
    }

    @Test
    void shouldRejectDuplicatedAccountAssociation() {

        UUID accountId =
                UUID.randomUUID();

        Employee first =
                new Employee(
                        UUID.randomUUID(),
                        "Ana",
                        "Torres"
                );

        first.associateAccount(
                accountId
        );

        employeeRepository
                .saveAndFlush(
                        first
                );

        Employee second =
                new Employee(
                        UUID.randomUUID(),
                        "Luis",
                        "García"
                );

        second.associateAccount(
                accountId
        );

        assertThatThrownBy(
                () ->
                        employeeRepository
                                .saveAndFlush(
                                        second
                                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                );
    }
}
