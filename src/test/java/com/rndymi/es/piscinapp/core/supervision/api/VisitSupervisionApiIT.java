package com.rndymi.es.piscinapp.core.supervision.api;

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
class VisitSupervisionApiIT {

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
    void shouldReturnCompleteSupervisionProjectionForAdmin()
            throws Exception {

        Fixture fixture =
                fixture();

        executeVisitWithObservationAndIncident(
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
                                "/api/v1/visits/{visitId}/supervision",
                                fixture.visit().getId()
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
                                "$.visit.id"
                        )
                                .value(
                                        fixture.visit()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.visit.poolId"
                        )
                                .value(
                                        fixture.pool()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.visit.crewId"
                        )
                                .value(
                                        fixture.crew()
                                                .getId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.execution.startedAt"
                        )
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath(
                                "$.activities",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.activities[0].executionStatus"
                        )
                                .value(
                                        "COMPLETED"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.observations",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.observations[0].text"
                        )
                                .value(
                                        "Water looked cloudy."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.incidents",
                                hasSize(
                                        1
                                )
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.incidents[0].description"
                        )
                                .value(
                                        "Pump cannot start."
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.incidents[0].status"
                        )
                                .value(
                                        "OPEN"
                                )
                );
    }

    @Test
    void shouldAllowCurrentCrewSupervisorToSupervise()
            throws Exception {

        Fixture fixture =
                fixture();

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{visitId}/supervision",
                                fixture.visit().getId()
                        )
                                .with(
                                        userJwt(
                                                fixture.supervisorUsername()
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldRejectOrdinaryCrewMemberSupervision()
            throws Exception {

        Fixture fixture =
                fixture();

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{visitId}/supervision",
                                fixture.visit().getId()
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
                                        "VISIT_SUPERVISION_FORBIDDEN"
                                )
                );
    }

    @Test
    void shouldRejectUnrelatedUserSupervision()
            throws Exception {

        Fixture fixture =
                fixture();

        Identity unrelated =
                createIdentity(
                        "unrelated"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/visits/{visitId}/supervision",
                                fixture.visit().getId()
                        )
                                .with(
                                        userJwt(
                                                unrelated.username()
                                        )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    private void executeVisitWithObservationAndIncident(
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

        mockMvc.perform(
                        post(
                                "/api/v1/visits/{visitId}/observations",
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
                                          "text": "Water looked cloudy."
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
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
                                          "description": "Pump cannot start."
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                );

        mockMvc.perform(
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
                )
                .andExpect(
                        status().isOk()
                );
    }

    private Fixture fixture() {

        SwimmingPool pool =
                swimmingPoolService.createPool(
                        "Central Pool",
                        "1 Main Street"
                );

        MaintenanceActivity activity =
                maintenanceActivityService.createActivity(
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

        Identity supervisor =
                createIdentity(
                        "supervisor"
                );

        Identity worker =
                createIdentity(
                        "worker"
                );

        crewService.addMember(
                crew.getId(),
                supervisor.employee().getId()
        );

        crewService.addMember(
                crew.getId(),
                worker.employee().getId()
        );

        crewService.assignSupervisor(
                crew.getId(),
                supervisor.employee().getId()
        );

        Visit visit =
                visitService.createVisit(
                        pool.getId(),
                        crew.getId(),
                        LocalDate.of(
                                2099,
                                9,
                                5
                        ),
                        LocalTime.of(
                                9,
                                0
                        ),
                        List.of(
                                activity.getId()
                        ),
                        "Operational visit"
                );

        return new Fixture(
                pool,
                activity,
                crew,
                visit,
                supervisor.username(),
                worker.username()
        );
    }

    private Identity createIdentity(
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

        UUID id =
                UUID.randomUUID();

        userAccountRepository.saveAndFlush(
                new UserAccount(
                        id,
                        username,
                        "{noop}unused-password",
                        true,
                        roles
                )
        );

        return id;
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
            SwimmingPool pool,
            MaintenanceActivity activity,
            Crew crew,
            Visit visit,
            String supervisorUsername,
            String workerUsername
    ) {
    }
}
