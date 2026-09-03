package com.rndymi.es.piscinapp.core.crews.api;

import com.rndymi.es.piscinapp.core.crews.application.CrewService;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeService;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
class CrewApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CrewService
            crewService;

    @Autowired
    private EmployeeService
            employeeService;

    @Autowired
    private CrewMembershipRepository
            crewMembershipRepository;

    @Autowired
    private CrewRepository
            crewRepository;

    @Autowired
    private EmployeeRepository
            employeeRepository;

    @BeforeEach
    void setUp() {

        crewMembershipRepository
                .deleteAll();

        crewRepository
                .deleteAll();

        employeeRepository
                .deleteAll();
    }

    @Test
    void shouldAllowAdminToCreateCrew()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/crews"
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
                                          "name": " Morning Crew "
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
                                                        "/api/v1/crews/"
                                                )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Morning Crew"
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
                                "$.supervisorEmployeeId"
                        )
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath(
                                "$.memberIds"
                        )
                                .isArray()
                )
                .andExpect(
                        jsonPath(
                                "$.memberIds",
                                hasSize(
                                        0
                                )
                        )
                );
    }

    @Test
    void shouldRejectBlankCrewName()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/crews"
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
                                          "name": "   "
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
    void shouldReturnCrew()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        mockMvc.perform(
                        get(
                                "/api/v1/crews/{id}",
                                crew.getId()
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
                                        crew
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.name"
                        )
                                .value(
                                        "Morning Crew"
                                )
                );
    }

    @Test
    void shouldReturnCrewNotFound()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/crews/{id}",
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
                                        "CREW_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldFilterAndSearchCrews()
            throws Exception {

        crewService
                .createCrew(
                        "Morning Crew"
                );

        Crew inactive =
                crewService
                        .createCrew(
                                "Night Crew"
                        );

        crewService
                .updateStatus(
                        inactive.getId(),
                        false
                );

        mockMvc.perform(
                        get(
                                "/api/v1/crews"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "active",
                                        "true"
                                )
                                .param(
                                        "search",
                                        "morning"
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
                                "$.content[0].name"
                        )
                                .value(
                                        "Morning Crew"
                                )
                );
    }

    @Test
    void shouldRejectUnsupportedSort()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/crews"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "sort",
                                        "supervisorEmployeeId,asc"
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
    void shouldAddMemberAndExposeMemberId()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/crews/{id}/members/{employeeId}",
                                crew.getId(),
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
                                "/api/v1/crews/{id}",
                                crew.getId()
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
                                "$.memberIds[0]"
                        )
                                .value(
                                        employee
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldRejectDuplicateMember()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/crews/{id}/members/{employeeId}",
                                crew.getId(),
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
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
                                        "CREW_MEMBERSHIP_CONFLICT"
                                )
                );
    }

    @Test
    void shouldRejectInactiveEmployeeAsNewMember()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        employeeService
                .updateStatus(
                        employee.getId(),
                        false
                );

        mockMvc.perform(
                        put(
                                "/api/v1/crews/{id}/members/{employeeId}",
                                crew.getId(),
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
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
                                        "RESOURCE_INACTIVE"
                                )
                );
    }

    @Test
    void shouldAssignAndClearSupervisor()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/crews/{id}/supervisor/{employeeId}",
                                crew.getId(),
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
                                "/api/v1/crews/{id}",
                                crew.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.supervisorEmployeeId"
                        )
                                .value(
                                        employee
                                                .getId()
                                                .toString()
                                )
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/crews/{id}/supervisor",
                                crew.getId()
                        )
                                .with(
                                        adminJwt()
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void shouldRejectSupervisorOutsideCrew()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        mockMvc.perform(
                        put(
                                "/api/v1/crews/{id}/supervisor/{employeeId}",
                                crew.getId(),
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
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
                                        "CREW_SUPERVISOR_CONFLICT"
                                )
                );
    }

    @Test
    void shouldRejectRemovingCurrentSupervisor()
            throws Exception {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        employee.getId()
                );

        mockMvc.perform(
                        delete(
                                "/api/v1/crews/{id}/members/{employeeId}",
                                crew.getId(),
                                employee.getId()
                        )
                                .with(
                                        adminJwt()
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
                                        "CREW_SUPERVISOR_CONFLICT"
                                )
                );
    }

    @Test
    void shouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/crews"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldRejectUserRole()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/crews"
                        )
                                .with(
                                        userJwt()
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor
    adminJwt() {

        return jwt()
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                );
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor
    userJwt() {

        return jwt()
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        )
                );
    }
}
