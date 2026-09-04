package com.rndymi.es.piscinapp.core.employees.persistence;

import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository
        extends JpaRepository<Employee, UUID>,
        JpaSpecificationExecutor<Employee> {

    boolean existsByUserAccountId(
            UUID userAccountId
    );

    boolean existsByUserAccountIdAndIdNot(
            UUID userAccountId,
            UUID employeeId
    );

    Optional<Employee> findByUserAccountId(
            UUID userAccountId
    );
}
