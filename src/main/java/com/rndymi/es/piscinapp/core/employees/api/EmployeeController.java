package com.rndymi.es.piscinapp.core.employees.api;

import com.rndymi.es.piscinapp.core.employees.api.dto.AssociateEmployeeAccountRequest;
import com.rndymi.es.piscinapp.core.employees.api.dto.CreateEmployeeRequest;
import com.rndymi.es.piscinapp.core.employees.api.dto.EmployeeResponse;
import com.rndymi.es.piscinapp.core.employees.api.dto.UpdateEmployeeRequest;
import com.rndymi.es.piscinapp.core.employees.api.dto.UpdateEmployeeStatusRequest;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeService;
import com.rndymi.es.piscinapp.core.platform.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/employees" )
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeePageRequestFactory pageRequestFactory;

    @PostMapping
    @Operation(
            summary =
                    "Create an operational employee"
    )
    public ResponseEntity<EmployeeResponse>
    createEmployee(
            @Valid
            @RequestBody
            CreateEmployeeRequest request
    ) {

        EmployeeResponse response =
                EmployeeResponse.from(
                        employeeService
                                .createEmployee(
                                        request.firstName(),
                                        request.familyName()
                                )
                );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/employees/"
                                        + response.id()
                        )
                )
                .body(
                        response
                );
    }

    @GetMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Return one operational employee"
    )
    public EmployeeResponse getEmployee(
            @PathVariable
            UUID id
    ) {

        return EmployeeResponse.from(
                employeeService
                        .getEmployee(
                                id
                        )
        );
    }

    @GetMapping
    @Operation(
            summary =
                    "Search operational employees"
    )
    public PageResponse<EmployeeResponse>
    listEmployees(
            @RequestParam(
                    defaultValue = "0"
            )
            int page,
            @RequestParam(
                    defaultValue = "20"
            )
            int size,
            @RequestParam(
                    required = false
            )
            String sort,
            @RequestParam(
                    required = false
            )
            Boolean active,
            @RequestParam(
                    required = false
            )
            String search
    ) {

        return PageResponse.from(
                employeeService
                        .listEmployees(
                                active,
                                search,
                                pageRequestFactory
                                        .create(
                                                page,
                                                size,
                                                sort
                                        )
                        ),
                EmployeeResponse::from
        );
    }

    @PutMapping(
            "/{id}"
    )
    @Operation(
            summary =
                    "Update employee identity fields"
    )
    public EmployeeResponse updateEmployee(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateEmployeeRequest request
    ) {

        return EmployeeResponse.from(
                employeeService
                        .updateEmployee(
                                id,
                                request.firstName(),
                                request.familyName()
                        )
        );
    }

    @PutMapping(
            "/{id}/status"
    )
    @Operation(
            summary =
                    "Activate or deactivate an employee"
    )
    public EmployeeResponse updateStatus(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateEmployeeStatusRequest request
    ) {

        return EmployeeResponse.from(
                employeeService
                        .updateStatus(
                                id,
                                request.active()
                        )
        );
    }

    @PutMapping(
            "/{id}/account"
    )
    @Operation(
            summary =
                    "Associate an existing security account with an employee"
    )
    public EmployeeResponse associateAccount(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            AssociateEmployeeAccountRequest request
    ) {

        return EmployeeResponse.from(
                employeeService
                        .associateAccount(
                                id,
                                request.userAccountId()
                        )
        );
    }

    @DeleteMapping(
            "/{id}/account"
    )
    @Operation(
            summary =
                    "Remove an employee account association"
    )
    public ResponseEntity<Void>
    removeAccountAssociation(
            @PathVariable
            UUID id
    ) {

        employeeService
                .removeAccountAssociation(
                        id
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}
