package com.rndymi.es.piscinapp.core.incidents.api;

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
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.persistence.IncidentRepository;
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
class IncidentApiIT {

    private static final LocalDate FUTURE_DATE =
            LocalDate.of(
                    2099,
                    9,
                    5
            );

    private static final LocalTime FUTURE_TIME =
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
    private IncidentRepository incidentRepository;

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

        incidentRepository.deleteAll();

        visitObservationRepository.deleteAll();

        visitMaintenanceActivityRepository.deleteAll();

        visitRepository.deleteAll();

        poolMaintenanceActivityRepository.deleteAll();

        crewMembershipRepository.deleteAll();

        crewRepository.deleteAll();

        employeeRepository.deleteAll();

        maintenanceActivityRepository.deleteAll();

        swimmingPoolRepository.deleteAll();

        userAccountRepository.deleteAll();
    }

    @Test
    void shouldCreateOpenIncidentForAssignedWorker() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        mockMvc.perform(
                        post(
                                "/api/v1/visits/{visitId}/incidents",
                                fixture.visit().getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.workerUsername()
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "description": "  Pump cannot start.  "
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.visitId"
                        )
                                .value(
                                        fixture.visit()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.description"
                        )
                                .value(
                                        "Pump cannot start."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "OPEN"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.createdByAccountId"
                        )
                                .value(
                                        fixture.workerAccountId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.createdByEmployeeId"
                        )
                                .value(
                                        fixture.worker()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.resolvedAt"
                        )
                                .doesNotExist()
                );
    }

    @Test
    void shouldRejectCreationBeforeVisitStarts() throws Exception {

        Fixture fixture =
                fixture();

        createIncident(
                fixture,
                fixture.workerUsername()
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
    void shouldRejectCreationAfterVisitCompletion() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        completeActivity(
                fixture
        );

        completeVisit(
                fixture
        );

        createIncident(
                fixture,
                fixture.workerUsername()
        )
                .andExpect(
                        status().isConflict()
                );
    }

    @Test
    void shouldRejectCreationForCancelledVisit() throws Exception {

        Fixture fixture =
                fixture();

        visitService.cancelVisit(
                fixture.visit().getId()
        );

        createIncident(
                fixture,
                fixture.workerUsername()
        )
                .andExpect(
                        status().isConflict()
                );
    }

    @Test
    void shouldRejectCreationForUnrelatedUser() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        Identity outsider =
                createIdentity(
                        "outsider",
                        Set.of(
                                SecurityRole.USER
                        )
                );

        createIncident(
                fixture,
                outsider.username()
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
    void shouldAllowCrewSupervisorToResolveIncident() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        Incident incident =
                createAndLoadIncident(
                        fixture
                );

        mockMvc.perform(
                        put(
                                "/api/v1/incidents/{incidentId}/resolve",
                                incident.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.supervisorUsername()
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
                                        "RESOLVED"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.resolvedByAccountId"
                        )
                                .value(
                                        fixture.supervisorAccountId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.resolvedByEmployeeId"
                        )
                                .value(
                                        fixture.supervisor()
                                                .getId()
                                                .toString()
                                )
                );
    }

    @Test
    void shouldRejectResolutionByOrdinaryCrewMember() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        Incident incident =
                createAndLoadIncident(
                        fixture
                );

        mockMvc.perform(
                        put(
                                "/api/v1/incidents/{incidentId}/resolve",
                                incident.getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.workerUsername()
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
                                        "INCIDENT_RESOLUTION_FORBIDDEN"
                                )
                );
    }

    @Test
    void shouldAllowAdminWithoutEmployeeToResolve() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        Incident incident =
                createAndLoadIncident(
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
                                "/api/v1/incidents/{incidentId}/resolve",
                                incident.getId()
                        )
                                .with(
                                        adminJwt(
                                                "admin"
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.resolvedByAccountId"
                        )
                                .value(
                                        adminAccountId
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.resolvedByEmployeeId"
                        )
                                .doesNotExist()
                );
    }

    @Test
    void shouldRejectDuplicateResolution() throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        Incident incident =
                createAndLoadIncident(
                        fixture
                );

        resolveAsSupervisor(
                fixture,
                incident
        );

        resolveAsSupervisor(
                fixture,
                incident
        )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath(
                                "$.code"
                        )
                                .value(
                                        "INCIDENT_STATE_CONFLICT"
                                )
                );
    }

    @Test
    void shouldCompleteVisitWithOpenIncidentAndResolveItLater()
            throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        Incident incident =
                createAndLoadIncident(
                        fixture
                );

        completeActivity(
                fixture
        );

        completeVisit(
                fixture
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

        Incident stillOpen =
                incidentRepository
                        .findById(
                                incident.getId()
                        )
                        .orElseThrow();

        org.assertj.core.api.Assertions
                .assertThat(
                        stillOpen.getStatus()
                )
                .isEqualTo(
                        com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus.OPEN
                );

        resolveAsSupervisor(
                fixture,
                incident
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.status"
                        )
                                .value(
                                        "RESOLVED"
                                )
                );
    }

    @Test
    void shouldListVisitIncidentsForAssignedWorker()
            throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        createAndLoadIncident(
                fixture
        );

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{visitId}/incidents",
                                fixture.visit().getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.workerUsername()
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
    void shouldAllowAdminIncidentSearchWithFilters()
            throws Exception {

        Fixture fixture =
                fixture();

        startVisit(
                fixture
        );

        createAndLoadIncident(
                fixture
        );

        createAccount(
                "admin",
                Set.of(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/incidents"
                        )
                                .queryParam(
                                        "status",
                                        "OPEN"
                                )
                                .queryParam(
                                        "visitId",
                                        fixture.visit()
                                                .getId()
                                                .toString()
                                )
                                .queryParam(
                                        "createdByEmployeeId",
                                        fixture.worker()
                                                .getId()
                                                .toString()
                                )
                                .with(
                                        adminJwt(
                                                "admin"
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
                );
    }

    @Test
    void shouldRejectInvalidIncidentSearchSort()
            throws Exception {

        createAccount(
                "admin",
                Set.of(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/incidents"
                        )
                                .queryParam(
                                        "sort",
                                        "visitId,asc"
                                )
                                .with(
                                        adminJwt(
                                                "admin"
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    private Fixture fixture() {

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

        configurationService.configure(
                pool.getId(),
                activity.getId()
        );

        Crew crew =
                crewService.createCrew(
                        "Morning Crew"
                );

        Identity supervisorIdentity =
                createIdentity(
                        "supervisor",
                        Set.of(
                                SecurityRole.USER
                        )
                );

        Identity workerIdentity =
                createIdentity(
                        "worker",
                        Set.of(
                                SecurityRole.USER
                        )
                );

        crewService.addMember(
                crew.getId(),
                supervisorIdentity.employee().getId()
        );

        crewService.addMember(
                crew.getId(),
                workerIdentity.employee().getId()
        );

        crewService.assignSupervisor(
                crew.getId(),
                supervisorIdentity.employee().getId()
        );

        Visit visit =
                visitService.createVisit(
                        pool.getId(),
                        crew.getId(),
                        FUTURE_DATE,
                        FUTURE_TIME,
                        List.of(
                                activity.getId()
                        ),
                        "Operational visit"
                );

        return new Fixture(
                visit,
                activity,
                supervisorIdentity.username(),
                supervisorIdentity.accountId(),
                supervisorIdentity.employee(),
                workerIdentity.username(),
                workerIdentity.accountId(),
                workerIdentity.employee()
        );
    }

    private Identity createIdentity(
            String username,
            Set<SecurityRole> roles
    ) {

        UUID accountId =
                createAccount(
                        username,
                        roles
                );

        Employee employee =
                employeeService.createEmployee(
                        "Employee",
                        username
                );

        employeeService.associateAccount(
                employee.getId(),
                accountId
        );

        return new Identity(
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

        userAccountRepository.saveAndFlush(
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

    private void startVisit(
            Fixture fixture
    )
            throws Exception {

        mockMvc.perform(
                        put(
                                "/api/v1/visits/{visitId}/start",
                                fixture.visit().getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.workerUsername()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    private org.springframework.test.web.servlet.ResultActions
    createIncident(
            Fixture fixture,
            String username
    )
            throws Exception {

        return mockMvc.perform(
                post(
                        "/api/v1/visits/{visitId}/incidents",
                        fixture.visit().getId()
                )
                        .with(
                                userJwt(
                                        username
                                )
                        )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                """
                                {
                                  "description": "Pump cannot start."
                                }
                                """
                        )
        );
    }

    private Incident createAndLoadIncident(
            Fixture fixture
    )
            throws Exception {

        createIncident(
                fixture,
                fixture.workerUsername()
        )
                .andExpect(
                        status().isCreated()
                );

        return incidentRepository
                .findAllByVisitIdOrderByCreatedAtAscIdAsc(
                        fixture.visit().getId()
                )
                .getFirst();
    }

    private org.springframework.test.web.servlet.ResultActions
    resolveAsSupervisor(
            Fixture fixture,
            Incident incident
    )
            throws Exception {

        return mockMvc.perform(
                put(
                        "/api/v1/incidents/{incidentId}/resolve",
                        incident.getId()
                )
                        .with(
                                userJwt(
                                        fixture.supervisorUsername()
                                )
                        )
        );
    }

    private org.springframework.test.web.servlet.ResultActions
    completeActivity(
            Fixture fixture
    )
            throws Exception {

        return mockMvc.perform(
                put(
                        "/api/v1/visits/{visitId}/activities/{activityId}/complete",
                        fixture.visit().getId(),
                        fixture.activity().getId()
                )
                        .with(
                                userJwt(
                                        fixture.workerUsername()
                                )
                        )
        );
    }

    private org.springframework.test.web.servlet.ResultActions
    completeVisit(
            Fixture fixture
    )
            throws Exception {

        return mockMvc.perform(
                put(
                        "/api/v1/visits/{visitId}/complete",
                        fixture.visit().getId()
                )
                        .with(
                                userJwt(
                                        fixture.workerUsername()
                                )
                        )
        );
    }

    private static RequestPostProcessor userJwt(
            String username
    ) {

        return jwt()
                .jwt(
                        token ->
                                token.subject(
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
                        token ->
                                token.subject(
                                        username
                                )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_USER"
                        ),
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                );
    }

    private record Identity(
            String username,
            UUID accountId,
            Employee employee
    ) {
    }

    private record Fixture(
            Visit visit,
            MaintenanceActivity activity,
            String supervisorUsername,
            UUID supervisorAccountId,
            Employee supervisor,
            String workerUsername,
            UUID workerAccountId,
            Employee worker
    ) {
    }
}
