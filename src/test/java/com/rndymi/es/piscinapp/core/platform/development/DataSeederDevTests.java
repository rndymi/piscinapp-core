package com.rndymi.es.piscinapp.core.platform.development;

import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.domain.CrewMembership;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.domain.PoolMaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.maintenance.persistence.PoolMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.domain.VisitMaintenanceActivity;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitRepository;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataSeederDevTests {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(
                    Instant.parse(
                            "2026-09-02T10:00:00Z"
                    ),
                    ZoneId.of(
                            "Europe/Madrid"
                    )
            );

    @Mock
    private VisitMaintenanceActivityRepository
            visitMaintenanceActivityRepository;

    @Mock
    private VisitRepository
            visitRepository;

    @Mock
    private PoolMaintenanceActivityRepository
            poolMaintenanceActivityRepository;

    @Mock
    private CrewMembershipRepository
            crewMembershipRepository;

    @Mock
    private CrewRepository
            crewRepository;

    @Mock
    private EmployeeRepository
            employeeRepository;

    @Mock
    private MaintenanceActivityRepository
            maintenanceActivityRepository;

    @Mock
    private SwimmingPoolRepository
            swimmingPoolRepository;

    private DataSeederDev
            seeder;

    @BeforeEach
    void setUp() {

        seeder =
                new DataSeederDev(
                        visitMaintenanceActivityRepository,
                        visitRepository,
                        poolMaintenanceActivityRepository,
                        crewMembershipRepository,
                        crewRepository,
                        employeeRepository,
                        maintenanceActivityRepository,
                        swimmingPoolRepository,
                        FIXED_CLOCK
                );
    }

    @Test
    void shouldResetOperationalDataBeforeSeeding() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        InOrder order =
                inOrder(
                        visitMaintenanceActivityRepository,
                        visitRepository,
                        poolMaintenanceActivityRepository,
                        crewMembershipRepository,
                        crewRepository,
                        employeeRepository,
                        maintenanceActivityRepository,
                        swimmingPoolRepository
                );

        order.verify(
                        visitMaintenanceActivityRepository
                )
                .deleteAllInBatch();

        order.verify(
                        visitRepository
                )
                .deleteAllInBatch();

        order.verify(
                        poolMaintenanceActivityRepository
                )
                .deleteAllInBatch();

        order.verify(
                        crewMembershipRepository
                )
                .deleteAllInBatch();

        order.verify(
                        crewRepository
                )
                .deleteAllInBatch();

        order.verify(
                        employeeRepository
                )
                .deleteAllInBatch();

        order.verify(
                        maintenanceActivityRepository
                )
                .deleteAllInBatch();

        order.verify(
                        swimmingPoolRepository
                )
                .deleteAllInBatch();

        order.verify(
                        swimmingPoolRepository
                )
                .saveAll(
                        org.mockito.ArgumentMatchers.any()
                );
    }

    @Test
    void shouldSeedDeterministicOperationalDataset() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        List<SwimmingPool> pools =
                captureSavedValues(
                        swimmingPoolRepository
                );

        assertThat(
                pools
        )
                .extracting(
                        SwimmingPool::getId
                )
                .containsExactly(
                        DataSeederDev.POOL_CENTRAL_ID,
                        DataSeederDev.POOL_NORTH_ID
                );

        assertThat(
                pools
        )
                .allMatch(
                        SwimmingPool::isActive
                );

        List<MaintenanceActivity> activities =
                captureSavedValues(
                        maintenanceActivityRepository
                );

        assertThat(
                activities
        )
                .hasSize(
                        4
                )
                .allMatch(
                        MaintenanceActivity::isActive
                );

        assertThat(
                activities
        )
                .extracting(
                        MaintenanceActivity::getId
                )
                .containsExactly(
                        DataSeederDev.ACTIVITY_FILTER_INSPECTION_ID,
                        DataSeederDev.ACTIVITY_WATER_QUALITY_ID,
                        DataSeederDev.ACTIVITY_SURFACE_CLEANING_ID,
                        DataSeederDev.ACTIVITY_PUMP_INSPECTION_ID
                );

        List<Employee> employees =
                captureSavedValues(
                        employeeRepository
                );

        assertThat(
                employees
        )
                .hasSize(
                        4
                )
                .allMatch(
                        Employee::isActive
                );

        assertThat(
                employees
        )
                .extracting(
                        Employee::getId
                )
                .containsExactly(
                        DataSeederDev.EMPLOYEE_ANA_ID,
                        DataSeederDev.EMPLOYEE_CARLOS_ID,
                        DataSeederDev.EMPLOYEE_LUCIA_ID,
                        DataSeederDev.EMPLOYEE_PEDRO_ID
                );

        List<Crew> crews =
                captureSavedValues(
                        crewRepository
                );

        assertThat(
                crews
        )
                .hasSize(
                        2
                )
                .allMatch(
                        Crew::isActive
                );

        assertThat(
                crews
        )
                .extracting(
                        Crew::getId
                )
                .containsExactly(
                        DataSeederDev.CREW_MORNING_ID,
                        DataSeederDev.CREW_AFTERNOON_ID
                );

        List<Visit> visits =
                captureSavedValues(
                        visitRepository
                );

        assertThat(
                visits
        )
                .extracting(
                        Visit::getId
                )
                .containsExactly(
                        DataSeederDev.VISIT_CENTRAL_ID,
                        DataSeederDev.VISIT_NORTH_ID
                );

        assertThat(
                visits
        )
                .extracting(
                        Visit::getStatus
                )
                .containsOnly(
                        VisitStatus.PLANNED
                );
    }

    @Test
    void shouldSeedAssignableCrewsWithSupervisorAmongMembers() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        List<Crew> crews =
                captureSavedValues(
                        crewRepository
                );

        List<CrewMembership> memberships =
                captureSavedValues(
                        crewMembershipRepository
                );

        Map<UUID, Set<UUID>> memberIdsByCrew =
                memberships.stream()
                        .collect(
                                Collectors.groupingBy(
                                        CrewMembership::getCrewId,
                                        Collectors.mapping(
                                                CrewMembership::getEmployeeId,
                                                Collectors.toSet()
                                        )
                                )
                        );

        assertThat(
                memberIdsByCrew.get(
                        DataSeederDev.CREW_MORNING_ID
                )
        )
                .containsExactlyInAnyOrder(
                        DataSeederDev.EMPLOYEE_ANA_ID,
                        DataSeederDev.EMPLOYEE_CARLOS_ID
                );

        assertThat(
                memberIdsByCrew.get(
                        DataSeederDev.CREW_AFTERNOON_ID
                )
        )
                .containsExactlyInAnyOrder(
                        DataSeederDev.EMPLOYEE_LUCIA_ID,
                        DataSeederDev.EMPLOYEE_PEDRO_ID
                );

        for (Crew crew : crews) {

            assertThat(
                    crew.getSupervisorEmployeeId()
            )
                    .isNotNull();

            assertThat(
                    memberIdsByCrew.get(
                            crew.getId()
                    )
            )
                    .contains(
                            crew.getSupervisorEmployeeId()
                    );
        }
    }

    @Test
    void shouldSeedVisitsOnlyWithActivitiesApplicableToTheirPool() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        List<PoolMaintenanceActivity> poolActivities =
                captureSavedValues(
                        poolMaintenanceActivityRepository
                );

        List<Visit> visits =
                captureSavedValues(
                        visitRepository
                );

        List<VisitMaintenanceActivity> visitActivities =
                captureSavedValues(
                        visitMaintenanceActivityRepository
                );

        Map<UUID, Set<UUID>> applicableActivitiesByPool =
                poolActivities.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PoolMaintenanceActivity::getPoolId,
                                        Collectors.mapping(
                                                PoolMaintenanceActivity::getMaintenanceActivityId,
                                                Collectors.toSet()
                                        )
                                )
                        );

        Map<UUID, Visit> visitsById =
                visits.stream()
                        .collect(
                                Collectors.toMap(
                                        Visit::getId,
                                        Function.identity()
                                )
                        );

        for (VisitMaintenanceActivity visitActivity : visitActivities) {

            Visit visit =
                    visitsById.get(
                            visitActivity.getVisitId()
                    );

            assertThat(
                    visit
            )
                    .isNotNull();

            assertThat(
                    applicableActivitiesByPool.get(
                            visit.getPoolId()
                    )
            )
                    .contains(
                            visitActivity.getMaintenanceActivityId()
                    );
        }
    }

    @Test
    void shouldSeedPlannedVisitsUsingInjectedClock() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        List<Visit> visits =
                captureSavedValues(
                        visitRepository
                );

        assertThat(
                visits
        )
                .extracting(
                        Visit::getPlannedDate
                )
                .containsExactly(
                        LocalDate.of(
                                2026,
                                9,
                                3
                        ),
                        LocalDate.of(
                                2026,
                                9,
                                4
                        )
                );

        assertThat(
                visits
        )
                .extracting(
                        Visit::getPlannedTime
                )
                .containsExactly(
                        LocalTime.of(
                                9,
                                30
                        ),
                        LocalTime.of(
                                11,
                                0
                        )
                );
    }

    @Test
    void shouldSeedDeterministicRelationshipIdentifiers() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        assertThat(
                captureSavedValues(
                        poolMaintenanceActivityRepository
                )
        )
                .extracting(
                        PoolMaintenanceActivity::getId
                )
                .containsExactly(
                        UUID.fromString(
                                "61000000-0000-0000-0000-000000000001"
                        ),
                        UUID.fromString(
                                "61000000-0000-0000-0000-000000000002"
                        ),
                        UUID.fromString(
                                "61000000-0000-0000-0000-000000000003"
                        ),
                        UUID.fromString(
                                "61000000-0000-0000-0000-000000000004"
                        ),
                        UUID.fromString(
                                "61000000-0000-0000-0000-000000000005"
                        ),
                        UUID.fromString(
                                "61000000-0000-0000-0000-000000000006"
                        )
                );

        assertThat(
                captureSavedValues(
                        crewMembershipRepository
                )
        )
                .extracting(
                        CrewMembership::getId
                )
                .containsExactly(
                        UUID.fromString(
                                "62000000-0000-0000-0000-000000000001"
                        ),
                        UUID.fromString(
                                "62000000-0000-0000-0000-000000000002"
                        ),
                        UUID.fromString(
                                "62000000-0000-0000-0000-000000000003"
                        ),
                        UUID.fromString(
                                "62000000-0000-0000-0000-000000000004"
                        )
                );

        assertThat(
                captureSavedValues(
                        visitMaintenanceActivityRepository
                )
        )
                .extracting(
                        VisitMaintenanceActivity::getId
                )
                .containsExactly(
                        UUID.fromString(
                                "63000000-0000-0000-0000-000000000001"
                        ),
                        UUID.fromString(
                                "63000000-0000-0000-0000-000000000002"
                        ),
                        UUID.fromString(
                                "63000000-0000-0000-0000-000000000003"
                        ),
                        UUID.fromString(
                                "63000000-0000-0000-0000-000000000004"
                        )
                );
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> captureSavedValues(
            org.springframework.data.repository.CrudRepository<T, UUID> repository
    ) {

        ArgumentCaptor<Iterable<T>> captor =
                ArgumentCaptor.forClass(
                        Iterable.class
                );

        verify(
                repository
        )
                .saveAll(
                        captor.capture()
                );

        return ((List<T>) captor.getValue());
    }
}
