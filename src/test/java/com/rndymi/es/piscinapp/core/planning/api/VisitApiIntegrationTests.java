package com.rndymi.es.piscinapp.core.planning.api;

import com.rndymi.es.piscinapp.core.crews.application.CrewService;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeService;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.maintenance.application.MaintenanceActivityService;
import com.rndymi.es.piscinapp.core.maintenance.application.PoolMaintenanceConfigurationService;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.maintenance.persistence.PoolMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.planning.application.VisitService;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitRepository;
import com.rndymi.es.piscinapp.core.pools.application.SwimmingPoolService;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class VisitApiIntegrationTests {

    private static final LocalDate
            FUTURE_DATE =
            LocalDate.of(
                    2099,
                    9,
                    3
            );

    private static final LocalTime
            FUTURE_TIME =
            LocalTime.of(
                    9,
                    30
            );

    @Autowired
    private MockMvc
            mockMvc;

    @Autowired
    private VisitService
            visitService;

    @Autowired
    private SwimmingPoolService
            swimmingPoolService;

    @Autowired
    private MaintenanceActivityService
            maintenanceActivityService;

    @Autowired
    private PoolMaintenanceConfigurationService
            configurationService;

    @Autowired
    private EmployeeService
            employeeService;

    @Autowired
    private CrewService
            crewService;

    @Autowired
    private VisitMaintenanceActivityRepository
            visitMaintenanceActivityRepository;

    @Autowired
    private VisitRepository
            visitRepository;

    @Autowired
    private PoolMaintenanceActivityRepository
            poolMaintenanceActivityRepository;

    @Autowired
    private CrewMembershipRepository
            crewMembershipRepository;

    @Autowired
    private CrewRepository
            crewRepository;

    @Autowired
    private EmployeeRepository
            employeeRepository;

    @Autowired
    private MaintenanceActivityRepository
            maintenanceActivityRepository;

    @Autowired
    private SwimmingPoolRepository
            swimmingPoolRepository;

    @BeforeEach
    void setUp() {

        visitMaintenanceActivityRepository
                .deleteAll();

        visitRepository
                .deleteAll();

        poolMaintenanceActivityRepository
                .deleteAll();

        crewMembershipRepository
                .deleteAll();

        crewRepository
                .deleteAll();

        employeeRepository
                .deleteAll();

        maintenanceActivityRepository
                .deleteAll();

        swimmingPoolRepository
                .deleteAll();
    }

    @Test
    void shouldAllowAdminToCreateVisit()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        mockMvc.perform(
                        post(
                                "/api/v1/visits"
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2099-09-03",
                                          "plannedTime": "09:30",
                                          "maintenanceActivityIds": [
                                            "%s"
                                          ],
                                          "notes": " Use side entrance. "
                                        }
                                        """
                                                .formatted(
                                                        fixture
                                                                .pool()
                                                                .getId(),
                                                        fixture
                                                                .crew()
                                                                .getId(),
                                                        fixture
                                                                .activity()
                                                                .getId()
                                                )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        header()
                                .string(
                                        "Location",
                                        startsWith(
                                                "/api/v1/visits/"
                                        )
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.poolId"
                        )
                                .value(
                                        fixture
                                                .pool()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.crewId"
                        )
                                .value(
                                        fixture
                                                .crew()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.plannedDate"
                        )
                                .value(
                                        "2099-09-03"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.plannedTime"
                        )
                                .value(
                                        "09:30"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "PLANNED"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.maintenanceActivityIds",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.maintenanceActivityIds[0]"
                        )
                                .value(
                                        fixture
                                                .activity()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.notes"
                        )
                                .value(
                                        "Use side entrance."
                                )
                );
    }

    @Test
    void shouldRejectUnauthenticatedVisitRequest()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void shouldRejectUserVisitRequest()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        userJwt()
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldReturnVisit()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit visit =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        FUTURE_TIME,
                        "Use side entrance."
                );

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{id}",
                                visit.getId()
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
                                        visit
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.poolId"
                        )
                                .value(
                                        fixture
                                                .pool()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.crewId"
                        )
                                .value(
                                        fixture
                                                .crew()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "PLANNED"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.maintenanceActivityIds",
                                hasSize(
                                        1
                                )
                        )
                );
    }

    @Test
    void shouldReturnVisitNotFound()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{id}",
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
                                        "VISIT_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldListVisitsByExactDate()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        createVisit(
                fixture,
                LocalDate.of(
                        2099,
                        9,
                        3
                ),
                LocalTime.of(
                        9,
                        30
                ),
                null
        );

        createVisit(
                fixture,
                LocalDate.of(
                        2099,
                        9,
                        4
                ),
                LocalTime.of(
                        9,
                        30
                ),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "date",
                                        "2099-09-03"
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
                                "$.content[0].plannedDate"
                        )
                                .value(
                                        "2099-09-03"
                                )
                );
    }

    @Test
    void shouldListVisitsByInclusiveDateRange()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        createVisit(
                fixture,
                LocalDate.of(
                        2099,
                        9,
                        1
                ),
                LocalTime.of(
                        8,
                        0
                ),
                null
        );

        createVisit(
                fixture,
                LocalDate.of(
                        2099,
                        9,
                        7
                ),
                LocalTime.of(
                        8,
                        0
                ),
                null
        );

        createVisit(
                fixture,
                LocalDate.of(
                        2099,
                        9,
                        8
                ),
                LocalTime.of(
                        8,
                        0
                ),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "fromDate",
                                        "2099-09-01"
                                )
                                .param(
                                        "toDate",
                                        "2099-09-07"
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
                );
    }

    @Test
    void shouldFilterVisitsByStatus()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        createVisit(
                fixture,
                LocalDate.of(
                        2099,
                        9,
                        3
                ),
                LocalTime.of(
                        9,
                        0
                ),
                null
        );

        Visit cancelled =
                createVisit(
                        fixture,
                        LocalDate.of(
                                2099,
                                9,
                                3
                        ),
                        LocalTime.of(
                                10,
                                0
                        ),
                        null
                );

        visitService
                .cancelVisit(
                        cancelled.getId()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "status",
                                        "CANCELLED"
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
                                "$.content[0].id"
                        )
                                .value(
                                        cancelled
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].status"
                        )
                                .value(
                                        "CANCELLED"
                                )
                );
    }

    @Test
    void shouldFilterVisitsByPool()
            throws Exception {

        PlanningFixture first =
                validPlanningFixture();

        createVisit(
                first,
                FUTURE_DATE,
                LocalTime.of(
                        9,
                        0
                ),
                null
        );

        SwimmingPool secondPool =
                swimmingPoolService
                        .createPool(
                                "North Pool",
                                "2 North Street"
                        );

        configurationService
                .configure(
                        secondPool.getId(),
                        first
                                .activity()
                                .getId()
                );

        PlanningFixture second =
                new PlanningFixture(
                        secondPool,
                        first.activity(),
                        first.crew()
                );

        createVisit(
                second,
                FUTURE_DATE,
                LocalTime.of(
                        10,
                        0
                ),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "poolId",
                                        secondPool
                                                .getId()
                                                .toString()
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
                                "$.content[0].poolId"
                        )
                                .value(
                                        secondPool
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldFilterVisitsByCrew()
            throws Exception {

        PlanningFixture first =
                validPlanningFixture();

        createVisit(
                first,
                FUTURE_DATE,
                LocalTime.of(
                        9,
                        0
                ),
                null
        );

        Crew secondCrew =
                createValidCrew(
                        "Afternoon Crew",
                        "Luis",
                        "Garcia"
                );

        PlanningFixture second =
                new PlanningFixture(
                        first.pool(),
                        first.activity(),
                        secondCrew
                );

        createVisit(
                second,
                FUTURE_DATE,
                LocalTime.of(
                        10,
                        0
                ),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "crewId",
                                        secondCrew
                                                .getId()
                                                .toString()
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
                                "$.content[0].crewId"
                        )
                                .value(
                                        secondCrew
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldPaginateAndSortVisits()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit later =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        LocalTime.of(
                                11,
                                0
                        ),
                        null
                );

        Visit earlier =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        LocalTime.of(
                                8,
                                0
                        ),
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                                .param(
                                        "sort",
                                        "plannedTime,desc"
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
                                "$.content[0].id"
                        )
                                .value(
                                        later
                                                .getId()
                                                .toString()
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
                                        1
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

        // The variable is intentionally retained to make the fixture's
        // second visit explicit and avoid accidental single-row coverage.
        org.assertj.core.api.Assertions
                .assertThat(
                        earlier.getId()
                )
                .isNotEqualTo(
                        later.getId()
                );
    }

    @Test
    void shouldRejectDateCombinedWithRange()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "date",
                                        "2099-09-03"
                                )
                                .param(
                                        "fromDate",
                                        "2099-09-01"
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
    void shouldRejectInvalidDateRange()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/visits"
                        )
                                .with(
                                        adminJwt()
                                )
                                .param(
                                        "fromDate",
                                        "2099-09-07"
                                )
                                .param(
                                        "toDate",
                                        "2099-09-01"
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
    void shouldRejectDuplicateActivities()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        UUID activityId =
                fixture
                        .activity()
                        .getId();

        mockMvc.perform(
                        post(
                                "/api/v1/visits"
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2099-09-03",
                                          "plannedTime": "09:30",
                                          "maintenanceActivityIds": [
                                            "%s",
                                            "%s"
                                          ]
                                        }
                                        """
                                                .formatted(
                                                        fixture
                                                                .pool()
                                                                .getId(),
                                                        fixture
                                                                .crew()
                                                                .getId(),
                                                        activityId,
                                                        activityId
                                                )
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
    void shouldRejectEmptyActivities()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        mockMvc.perform(
                        post(
                                "/api/v1/visits"
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2099-09-03",
                                          "plannedTime": "09:30",
                                          "maintenanceActivityIds": []
                                        }
                                        """
                                                .formatted(
                                                        fixture
                                                                .pool()
                                                                .getId(),
                                                        fixture
                                                                .crew()
                                                                .getId()
                                                )
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
    void shouldRejectPastSchedule()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        mockMvc.perform(
                        post(
                                "/api/v1/visits"
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2000-01-01",
                                          "plannedTime": "09:30",
                                          "maintenanceActivityIds": [
                                            "%s"
                                          ]
                                        }
                                        """
                                                .formatted(
                                                        fixture
                                                                .pool()
                                                                .getId(),
                                                        fixture
                                                                .crew()
                                                                .getId(),
                                                        fixture
                                                                .activity()
                                                                .getId()
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
                                        "VISIT_INVALID_SCHEDULE"
                                )
                );
    }

    @Test
    void shouldRejectInactivePool()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        swimmingPoolService
                .updateStatus(
                        fixture
                                .pool()
                                .getId(),
                        false
                );

        performCreate(
                fixture
                        .pool()
                        .getId(),
                fixture
                        .crew()
                        .getId(),
                fixture
                        .activity()
                        .getId()
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
    void shouldRejectMissingCrew()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        performCreate(
                fixture
                        .pool()
                        .getId(),
                UUID.randomUUID(),
                fixture
                        .activity()
                        .getId()
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
    void shouldRejectInactiveCrew()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        crewService
                .updateStatus(
                        fixture
                                .crew()
                                .getId(),
                        false
                );

        performCreate(
                fixture
                        .pool()
                        .getId(),
                fixture
                        .crew()
                        .getId(),
                fixture
                        .activity()
                        .getId()
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VISIT_CREW_NOT_ASSIGNABLE"
                                )
                );
    }

    @Test
    void shouldRejectCrewWithoutMembers()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Crew emptyCrew =
                crewService
                        .createCrew(
                                "Empty Crew"
                        );

        performCreate(
                fixture
                        .pool()
                        .getId(),
                emptyCrew.getId(),
                fixture
                        .activity()
                        .getId()
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VISIT_CREW_NOT_ASSIGNABLE"
                                )
                );
    }

    @Test
    void shouldRejectCrewWithoutSupervisor()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Crew crew =
                crewService
                        .createCrew(
                                "Crew Without Supervisor"
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

        performCreate(
                fixture
                        .pool()
                        .getId(),
                crew.getId(),
                fixture
                        .activity()
                        .getId()
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VISIT_CREW_NOT_ASSIGNABLE"
                                )
                );
    }

    @Test
    void shouldRejectInactiveCrewMember()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Employee secondMember =
                employeeService
                        .createEmployee(
                                "Carlos",
                                "Lopez"
                        );

        crewService
                .addMember(
                        fixture
                                .crew()
                                .getId(),
                        secondMember.getId()
                );

        employeeService
                .updateStatus(
                        secondMember.getId(),
                        false
                );

        performCreate(
                fixture
                        .pool()
                        .getId(),
                fixture
                        .crew()
                        .getId(),
                fixture
                        .activity()
                        .getId()
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VISIT_CREW_NOT_ASSIGNABLE"
                                )
                );
    }

    @Test
    void shouldRejectInactiveSupervisor()
            throws Exception {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Central Pool",
                                "1 Main Street"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                "Check filter condition"
                        );

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee supervisor =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        supervisor.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        supervisor.getId()
                );

        employeeService
                .updateStatus(
                        supervisor.getId(),
                        false
                );

        performCreate(
                pool.getId(),
                crew.getId(),
                activity.getId()
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VISIT_CREW_NOT_ASSIGNABLE"
                                )
                );
    }

    @Test
    void shouldRejectMissingActivity()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        performCreate(
                fixture
                        .pool()
                        .getId(),
                fixture
                        .crew()
                        .getId(),
                UUID.randomUUID()
        )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "MAINTENANCE_ACTIVITY_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldRejectInactiveActivity()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        maintenanceActivityService
                .updateStatus(
                        fixture
                                .activity()
                                .getId(),
                        false
                );

        performCreate(
                fixture
                        .pool()
                        .getId(),
                fixture
                        .crew()
                        .getId(),
                fixture
                        .activity()
                        .getId()
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
    void shouldRejectInapplicableActivity()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        MaintenanceActivity inapplicable =
                maintenanceActivityService
                        .createActivity(
                                "Deck inspection",
                                null
                        );

        performCreate(
                fixture
                        .pool()
                        .getId(),
                fixture
                        .crew()
                        .getId(),
                inapplicable.getId()
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "VISIT_ACTIVITY_NOT_APPLICABLE"
                                )
                );
    }

    @Test
    void shouldUpdatePlannedVisit()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit visit =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        FUTURE_TIME,
                        "Original notes"
                );

        MaintenanceActivity secondActivity =
                maintenanceActivityService
                        .createActivity(
                                "Water quality check",
                                null
                        );

        configurationService
                .configure(
                        fixture
                                .pool()
                                .getId(),
                        secondActivity.getId()
                );

        Crew replacementCrew =
                createValidCrew(
                        "Replacement Crew",
                        "Luis",
                        "Garcia"
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{id}",
                                visit.getId()
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2099-09-04",
                                          "plannedTime": "11:15",
                                          "maintenanceActivityIds": [
                                            "%s"
                                          ],
                                          "notes": " Updated notes "
                                        }
                                        """
                                                .formatted(
                                                        fixture
                                                                .pool()
                                                                .getId(),
                                                        replacementCrew
                                                                .getId(),
                                                        secondActivity
                                                                .getId()
                                                )
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
                                        visit
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.crewId"
                        )
                                .value(
                                        replacementCrew
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.plannedDate"
                        )
                                .value(
                                        "2099-09-04"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.plannedTime"
                        )
                                .value(
                                        "11:15"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.maintenanceActivityIds",
                                contains(
                                        secondActivity
                                                .getId()
                                                .toString()
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.notes"
                        )
                                .value(
                                        "Updated notes"
                                )
                );
    }

    @Test
    void shouldRevalidateUpdatedPool()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit visit =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        FUTURE_TIME,
                        null
                );

        SwimmingPool inactivePool =
                swimmingPoolService
                        .createPool(
                                "Inactive Pool",
                                "3 Side Street"
                        );

        configurationService
                .configure(
                        inactivePool.getId(),
                        fixture
                                .activity()
                                .getId()
                );

        swimmingPoolService
                .updateStatus(
                        inactivePool.getId(),
                        false
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{id}",
                                visit.getId()
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2099-09-04",
                                          "plannedTime": "11:15",
                                          "maintenanceActivityIds": [
                                            "%s"
                                          ]
                                        }
                                        """
                                                .formatted(
                                                        inactivePool
                                                                .getId(),
                                                        fixture
                                                                .crew()
                                                                .getId(),
                                                        fixture
                                                                .activity()
                                                                .getId()
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
                                        "RESOURCE_INACTIVE"
                                )
                );
    }

    @Test
    void shouldCancelPlannedVisit()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit visit =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        FUTURE_TIME,
                        null
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{id}/cancel",
                                visit.getId()
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
                                        visit
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "CANCELLED"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.maintenanceActivityIds",
                                hasSize(
                                        1
                                )
                        )
                );
    }

    @Test
    void shouldRejectUpdateAfterCancellation()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit visit =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        FUTURE_TIME,
                        null
                );

        visitService
                .cancelVisit(
                        visit.getId()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{id}",
                                visit.getId()
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
                                          "poolId": "%s",
                                          "crewId": "%s",
                                          "plannedDate": "2099-09-04",
                                          "plannedTime": "11:15",
                                          "maintenanceActivityIds": [
                                            "%s"
                                          ]
                                        }
                                        """
                                                .formatted(
                                                        fixture
                                                                .pool()
                                                                .getId(),
                                                        fixture
                                                                .crew()
                                                                .getId(),
                                                        fixture
                                                                .activity()
                                                                .getId()
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
                                        "VISIT_STATE_CONFLICT"
                                )
                );
    }

    @Test
    void shouldRejectRepeatedCancellation()
            throws Exception {

        PlanningFixture fixture =
                validPlanningFixture();

        Visit visit =
                createVisit(
                        fixture,
                        FUTURE_DATE,
                        FUTURE_TIME,
                        null
                );

        visitService
                .cancelVisit(
                        visit.getId()
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{id}/cancel",
                                visit.getId()
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
                                        "VISIT_STATE_CONFLICT"
                                )
                );
    }

    private PlanningFixture validPlanningFixture() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Central Pool",
                                "1 Main Street"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                "Check filter condition"
                        );

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        Crew crew =
                createValidCrew(
                        "Morning Crew",
                        "Ana",
                        "Martinez"
                );

        return new PlanningFixture(
                pool,
                activity,
                crew
        );
    }

    private Crew createValidCrew(
            String crewName,
            String firstName,
            String familyName
    ) {

        Crew crew =
                crewService
                        .createCrew(
                                crewName
                        );

        Employee supervisor =
                employeeService
                        .createEmployee(
                                firstName,
                                familyName
                        );

        crewService
                .addMember(
                        crew.getId(),
                        supervisor.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        supervisor.getId()
                );

        return crew;
    }

    private Visit createVisit(
            PlanningFixture fixture,
            LocalDate plannedDate,
            LocalTime plannedTime,
            String notes
    ) {

        return visitService
                .createVisit(
                        fixture
                                .pool()
                                .getId(),
                        fixture
                                .crew()
                                .getId(),
                        plannedDate,
                        plannedTime,
                        List.of(
                                fixture
                                        .activity()
                                        .getId()
                        ),
                        notes
                );
    }

    private org.springframework.test.web.servlet.ResultActions
    performCreate(
            UUID poolId,
            UUID crewId,
            UUID activityId
    )
            throws Exception {

        return mockMvc.perform(
                post(
                        "/api/v1/visits"
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
                                  "poolId": "%s",
                                  "crewId": "%s",
                                  "plannedDate": "2099-09-03",
                                  "plannedTime": "09:30",
                                  "maintenanceActivityIds": [
                                    "%s"
                                  ]
                                }
                                """
                                        .formatted(
                                                poolId,
                                                crewId,
                                                activityId
                                        )
                        )
        );
    }

    private static RequestPostProcessor adminJwt() {

        return jwt()
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                );
    }

    private static RequestPostProcessor userJwt() {

        return jwt()
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        )
                );
    }

    private record PlanningFixture(
            SwimmingPool pool,
            MaintenanceActivity activity,
            Crew crew
    ) {
    }
}
