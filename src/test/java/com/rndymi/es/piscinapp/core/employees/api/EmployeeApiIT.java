package com.rndymi.es.piscinapp.core.employees.api;

import com.rndymi.es.piscinapp.core.employees.application.EmployeeService;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiIT {

    private static final String
            PASSWORD =
            "employee-password-123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService
            employeeService;

    @Autowired
    private EmployeeRepository
            employeeRepository;

    @Autowired
    private UserAccountService
            userAccountService;

    @Autowired
    private UserAccountRepository
            userAccountRepository;

    @BeforeEach
    void setUp() {

        employeeRepository
                .deleteAll();

        userAccountRepository
                .deleteAll();
    }

    @Test
    void shouldAllowAdminToCreateEmployee()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/employees"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "firstName": " Ana ",
                                          "familyName": " Torres Ruiz "
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        header()
                                .string(
                                        "Location",
                                        org.hamcrest.Matchers
                                                .startsWith(
                                                        "/api/v1/employees/"
                                                )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.firstName"
                        )
                                .value(
                                        "Ana"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.familyName"
                        )
                                .value(
                                        "Torres Ruiz"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.displayName"
                        )
                                .value(
                                        "Ana Torres Ruiz"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        true
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.userAccountId"
                        )
                                .doesNotExist()
                );
    }

    @Test
    void shouldRejectBlankEmployeeName()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/employees"
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "firstName": "   ",
                                          "familyName": "Torres"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldReturnEmployee()
            throws Exception {

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        mockMvc.perform(
                        get(
                                "/api/v1/employees/{id}",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.id"
                        )
                                .value(
                                        employee
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldReturnEmployeeNotFound()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees/{id}",
                                UUID.randomUUID()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "EMPLOYEE_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldUpdateEmployee()
            throws Exception {

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/employees/{id}",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "firstName": "María José",
                                          "familyName": "Pérez Gómez"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.displayName"
                        )
                                .value(
                                        "María José Pérez Gómez"
                                )
                );
    }

    @Test
    void shouldDeactivateAndReactivateEmployee()
            throws Exception {

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/employees/{id}/status",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "active": false
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        false
                                )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/employees/{id}/status",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "active": true
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.active"
                        )
                                .value(
                                        true
                                )
                );
    }

    @Test
    void shouldAssociateExistingAccount()
            throws Exception {

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        UserAccount account =
                createAccount(
                        "employee.user"
                );

        mockMvc.perform(
                        put(
                                "/api/v1/employees/{id}/account",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "userAccountId": "%s"
                                        }
                                        """
                                                .formatted(
                                                        account.getId()
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.userAccountId"
                        )
                                .value(
                                        account
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldRejectUnknownAccountAssociation()
            throws Exception {

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/employees/{id}/account",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "userAccountId": "%s"
                                        }
                                        """
                                                .formatted(
                                                        UUID.randomUUID()
                                                )
                                )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "IDENTITY_USER_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldRejectAccountAssociatedWithAnotherEmployee()
            throws Exception {

        UserAccount account =
                createAccount(
                        "shared.user"
                );

        Employee first =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        Employee second =
                employeeService
                        .createEmployee(
                                "Luis",
                                "García"
                        );

        employeeService
                .associateAccount(
                        first.getId(),
                        account.getId()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/employees/{id}/account",
                                second.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "userAccountId": "%s"
                                        }
                                        """
                                                .formatted(
                                                        account.getId()
                                                )
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "EMPLOYEE_ACCOUNT_CONFLICT"
                                )
                );
    }

    @Test
    void shouldRemoveAccountAssociation()
            throws Exception {

        UserAccount account =
                createAccount(
                        "removable.user"
                );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        employeeService
                .associateAccount(
                        employee.getId(),
                        account.getId()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/employees/{id}/account",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNoContent()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/employees/{id}",
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.userAccountId"
                        )
                                .doesNotExist()
                );
    }

    @Test
    void shouldReturnPaginatedEmployees()
            throws Exception {

        employeeService
                .createEmployee(
                        "Ana",
                        "Torres"
                );

        employeeService
                .createEmployee(
                        "Luis",
                        "García"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/employees"
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content",
                                hasSize(
                                        2
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.page"
                        )
                                .value(
                                        0
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.size"
                        )
                                .value(
                                        20
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.totalElements"
                        )
                                .value(
                                        2
                                )
                );
    }

    @Test
    void shouldFilterEmployeesByActiveStateAndName()
            throws Exception {

        Employee inactive =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Torres"
                        );

        employeeService
                .updateStatus(
                        inactive.getId(),
                        false
                );

        employeeService
                .createEmployee(
                        "Luis",
                        "García"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/employees"
                        )
                                .queryParam(
                                        "active",
                                        "false"
                                )
                                .queryParam(
                                        "search",
                                        "TORRES"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.content",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].firstName"
                        )
                                .value(
                                        "Ana"
                                )
                );
    }

    @Test
    void shouldRejectPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees"
                        )
                                .queryParam(
                                        "size",
                                        "101"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectUnsupportedSort()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees"
                        )
                                .queryParam(
                                        "sort",
                                        "userAccountId,asc"
                                )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VALIDATION_ERROR"
                                )
                );
    }

    @Test
    void shouldRejectEmployeeAdministrationForNormalUser()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees"
                        )
                                .with(
                                        userJwt()
                                )
                )
                .andExpect(
                        status().isForbidden()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "ACCESS_DENIED"
                                )
                );
    }

    @Test
    void shouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "AUTHENTICATION_REQUIRED"
                                )
                );
    }

    private UserAccount createAccount(
            String username
    ) {

        return userAccountService
                .createAccount(
                        username,
                        PASSWORD,
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor
    adminJwt() {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        "admin.user"
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                );
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor
    userJwt() {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        "normal.user"
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        )
                );
    }
}
