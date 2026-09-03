package com.rndymi.es.piscinapp.core.execution.api;

import com.rndymi.es.piscinapp.core.crews.application.CrewService;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeService;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.execution.persistence.VisitObservationRepository;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
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
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class VisitExecutionApiIT {

    private static final LocalDate
            FUTURE_DATE =
            LocalDate.of(
                    2099,
                    9,
                    4
            );

    private static final LocalTime
            FUTURE_TIME =
            LocalTime.of(
                    9,
                    0
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwimmingPoolService swimmingPoolService;

    @Autowired
    private MaintenanceActivityService maintenanceActivityService;

    @Autowired
    private PoolMaintenanceConfigurationService configurationService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CrewService crewService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private VisitObservationRepository visitObservationRepository;

    @Autowired
    private VisitMaintenanceActivityRepository visitMaintenanceActivityRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PoolMaintenanceActivityRepository poolMaintenanceActivityRepository;

    @Autowired
    private CrewMembershipRepository crewMembershipRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MaintenanceActivityRepository maintenanceActivityRepository;

    @Autowired
    private SwimmingPoolRepository swimmingPoolRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    void setUp() {

        visitObservationRepository
                .deleteAll();

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

        userAccountRepository
                .deleteAll();
    }

    @Test
    void shouldReturnOnlyAssignedVisits() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        createVisit(
                fixture
        );

        Crew unrelatedCrew =
                createCrewWithSupervisor(
                        "Unrelated Crew"
                );

        visitService
                .createVisit(
                        fixture.pool().getId(),
                        unrelatedCrew.getId(),
                        FUTURE_DATE,
                        LocalTime.of(
                                10,
                                0
                        ),
                        List.of(
                                fixture.activity().getId()
                        ),
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/visits/assigned"
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
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
                                        fixture
                                                .crew()
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldStartAssignedVisitAndPersistActor() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        Visit visit =
                createVisit(
                        fixture
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/start",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "IN_PROGRESS"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.startedByAccountId"
                        )
                                .value(
                                        fixture
                                                .accountId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.startedByEmployeeId"
                        )
                                .value(
                                        fixture
                                                .employee()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.startedAt"
                        )
                                .isNotEmpty()
                );
    }

    @Test
    void shouldRejectUnassignedUserExecution() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        Visit visit =
                createVisit(
                        fixture
                );

        ExecutionIdentity outsider =
                createOperationalIdentity(
                        "outsider"
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/start",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                outsider.username()
                                        )
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
                                        "VISIT_EXECUTION_FORBIDDEN"
                                )
                );
    }

    @Test
    void shouldNotGiveAdminAutomaticExecutionBypass() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        Visit visit =
                createVisit(
                        fixture
                );

        UUID adminAccountId =
                createAccount(
                        "admin",
                        Set.of(
                                SecurityRole.USER,
                                SecurityRole.ADMIN
                        )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/start",
                                visit.getId()
                        )
                                .with(
                                        adminJwt(
                                                "admin"
                                        )
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
                                        "VISIT_EXECUTION_FORBIDDEN"
                                )
                );

        org.assertj.core.api.Assertions
                .assertThat(
                        adminAccountId
                )
                .isNotNull();
    }

    @Test
    void shouldRecordObservationOnlyDuringExecution() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        Visit visit =
                createVisit(
                        fixture
                );

        mockMvc.perform(
                        post(
                                "/api/v1/visits/{visitId}/observations",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "text": "Too early"
                                        }
                                        """
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

        start(
                visit,
                fixture
        );

        mockMvc.perform(
                        post(
                                "/api/v1/visits/{visitId}/observations",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "text": "  Filter housing shows visible wear.  "
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.text"
                        )
                                .value(
                                        "Filter housing shows visible wear."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.createdByAccountId"
                        )
                                .value(
                                        fixture
                                                .accountId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.createdByEmployeeId"
                        )
                                .value(
                                        fixture
                                                .employee()
                                                .getId()
                                                .toString()
                                )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{visitId}/observations",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$",
                                hasSize(
                                        1
                                )
                        )
                );
    }

    @Test
    void shouldRequireAllActivitiesBeforeCompletingVisit() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        Visit visit =
                createVisit(
                        fixture
                );

        start(
                visit,
                fixture
        );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/complete",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
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
                                        "VISIT_ACTIVITIES_PENDING"
                                )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/activities/{activityId}/complete",
                                visit.getId(),
                                fixture.activity().getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "COMPLETED"
                                )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/complete",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "COMPLETED"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.completedByAccountId"
                        )
                                .value(
                                        fixture
                                                .accountId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldKeepPlanningMutationAdminOnly() throws Exception {

        ExecutionFixture fixture =
                fixture(
                        "worker"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/visits"
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
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
                                          "plannedTime": "09:00",
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
                        status().isForbidden()
                );
    }

    private ExecutionFixture fixture(
            String username
    ) {

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
                createCrewWithSupervisor(
                        "Morning Crew"
                );

        ExecutionIdentity identity =
                createOperationalIdentity(
                        username
                );

        crewService
                .addMember(
                        crew.getId(),
                        identity
                                .employee()
                                .getId()
                );

        return new ExecutionFixture(
                username,
                identity.accountId(),
                identity.employee(),
                pool,
                activity,
                crew
        );
    }

    private Crew createCrewWithSupervisor(
            String name
    ) {

        Crew crew =
                crewService
                        .createCrew(
                                name
                        );

        Employee supervisor =
                employeeService
                        .createEmployee(
                                "Supervisor",
                                UUID.randomUUID()
                                        .toString()
                                        .substring(
                                                0,
                                                8
                                        )
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

    private ExecutionIdentity createOperationalIdentity(
            String username
    ) {

        UUID accountId =
                createAccount(
                        username,
                        Set.of(
                                SecurityRole.USER
                        )
                );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Field",
                                username
                        );

        employeeService
                .associateAccount(
                        employee.getId(),
                        accountId
                );

        return new ExecutionIdentity(
                username,
                accountId,
                employee
        );
    }

    private UUID createAccount(
            String username,
            Set<SecurityRole> roles
    ) {

        UUID accountId =
                UUID.randomUUID();

        userAccountRepository
                .saveAndFlush(
                        new UserAccount(
                                accountId,
                                username,
                                "{noop}unused-password",
                                true,
                                roles
                        )
                );

        return accountId;
    }

    private Visit createVisit(
            ExecutionFixture fixture
    ) {

        return visitService
                .createVisit(
                        fixture.pool().getId(),
                        fixture.crew().getId(),
                        FUTURE_DATE,
                        FUTURE_TIME,
                        List.of(
                                fixture
                                        .activity()
                                        .getId()
                        ),
                        null
                );
    }

    private void start(
            Visit visit,
            ExecutionFixture fixture
    )
            throws Exception {

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/start",
                                visit.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.username()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    private static RequestPostProcessor userJwt(
            String username
    ) {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        username
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        )
                );
    }

    private static RequestPostProcessor adminJwt(
            String username
    ) {

        return jwt()
                .jwt(
                        jwt ->
                                jwt.subject(
                                        username
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        ),
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        )
                );
    }

    private record ExecutionIdentity(
            String username,
            UUID accountId,
            Employee employee
    ) {
    }

    private record ExecutionFixture(
            String username,
            UUID accountId,
            Employee employee,
            SwimmingPool pool,
            MaintenanceActivity activity,
            Crew crew
    ) {
    }
}
