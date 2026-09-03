package com.rndymi.es.piscinapp.core.platform.development;

import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.domain.CrewMembership;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.employees.persistence.EmployeeRepository;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederDevTest {

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
    private UserAccountRepository
            userAccountRepository;

    @Mock
    private PasswordEncoder
            passwordEncoder;

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
                        userAccountRepository,
                        passwordEncoder,
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

        when(
                userAccountRepository
                        .findAllByOwnerFalse()
        )
                .thenReturn(
                        List.of()
                );

        when(
                passwordEncoder.encode(
                        anyString()
                )
        )
                .thenAnswer(
                        invocation ->
                                "encoded:"
                                        + invocation.getArgument(
                                        0,
                                        String.class
                                )
                );
    }

    @Test
    void shouldSeedDevelopmentAccountsBeforeOperationalFixtures() {

        InOrder order =
                inOrder(
                        userAccountRepository,
                        swimmingPoolRepository
                );

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        order.verify(
                        userAccountRepository
                )
                .findAllByOwnerFalse();

        order.verify(
                        userAccountRepository
                )
                .deleteAll(
                        List.of()
                );

        order.verify(
                        userAccountRepository
                )
                .flush();

        order.verify(
                        userAccountRepository
                )
                .saveAll(
                        anyList()
                );

        order.verify(
                        userAccountRepository
                )
                .flush();

        order.verify(
                        swimmingPoolRepository
                )
                .saveAll(
                        anyList()
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

    @Test
    void shouldSeedDeterministicDevelopmentAccounts() {

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserAccount>> captor =
                ArgumentCaptor.forClass(
                        List.class
                );

        verify(
                userAccountRepository
        )
                .saveAll(
                        captor.capture()
                );

        verify(
                passwordEncoder
        )
                .encode(
                        DataSeederDev.DEV_ADMIN_PASSWORD
                );

        verify(
                passwordEncoder
        )
                .encode(
                        DataSeederDev.DEV_USER_PASSWORD
                );

        List<UserAccount> accounts =
                captor.getValue();

        assertThat(
                accounts
        )
                .extracting(
                        UserAccount::getId
                )
                .containsExactly(
                        DataSeederDev.DEV_ADMIN_ACCOUNT_ID,
                        DataSeederDev.DEV_USER_ACCOUNT_ID
                );

        assertThat(
                accounts
        )
                .allMatch(
                        account ->
                                !account.isOwner()
                );

        UserAccount admin =
                accounts.getFirst();

        assertThat(
                admin.getRoles()
        )
                .containsExactlyInAnyOrder(
                        SecurityRole.USER,
                        SecurityRole.ADMIN
                );

        UserAccount user =
                accounts.get(1);

        assertThat(
                user.getRoles()
        )
                .containsExactly(
                        SecurityRole.USER
                );
    }

    @Test
    void shouldPreserveProtectedOwnerDuringReset() {

        UserAccount normalAccount =
                new UserAccount(
                        UUID.randomUUID(),
                        "temporary.user",
                        "encoded-password",
                        true,
                        EnumSet.of(
                                SecurityRole.USER
                        )
                );

        when(
                userAccountRepository
                        .findAllByOwnerFalse()
        )
                .thenReturn(
                        List.of(
                                normalAccount
                        )
                );

        seeder.run(
                mock(
                        ApplicationArguments.class
                )
        );

        verify(
                userAccountRepository
        )
                .deleteAll(
                        List.of(
                                normalAccount
                        )
                );

        verify(
                userAccountRepository,
                never()
        )
                .deleteAllInBatch();
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
