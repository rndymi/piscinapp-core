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
import com.rndymi.es.piscinapp.core.planning.persistence.VisitMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitRepository;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import com.rndymi.es.piscinapp.core.pools.persistence.SwimmingPoolRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Component
@Profile("dev")
public class DataSeederDev
        implements ApplicationRunner {

    static final UUID POOL_CENTRAL_ID =
            UUID.fromString(
                    "10000000-0000-0000-0000-000000000001"
            );

    static final UUID POOL_NORTH_ID =
            UUID.fromString(
                    "10000000-0000-0000-0000-000000000002"
            );

    static final UUID ACTIVITY_FILTER_INSPECTION_ID =
            UUID.fromString(
                    "20000000-0000-0000-0000-000000000001"
            );

    static final UUID ACTIVITY_WATER_QUALITY_ID =
            UUID.fromString(
                    "20000000-0000-0000-0000-000000000002"
            );

    static final UUID ACTIVITY_SURFACE_CLEANING_ID =
            UUID.fromString(
                    "20000000-0000-0000-0000-000000000003"
            );

    static final UUID ACTIVITY_PUMP_INSPECTION_ID =
            UUID.fromString(
                    "20000000-0000-0000-0000-000000000004"
            );

    static final UUID EMPLOYEE_ANA_ID =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000001"
            );

    static final UUID EMPLOYEE_CARLOS_ID =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000002"
            );

    static final UUID EMPLOYEE_LUCIA_ID =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000003"
            );

    static final UUID EMPLOYEE_PEDRO_ID =
            UUID.fromString(
                    "30000000-0000-0000-0000-000000000004"
            );

    static final UUID CREW_MORNING_ID =
            UUID.fromString(
                    "40000000-0000-0000-0000-000000000001"
            );

    static final UUID CREW_AFTERNOON_ID =
            UUID.fromString(
                    "40000000-0000-0000-0000-000000000002"
            );

    static final UUID VISIT_CENTRAL_ID =
            UUID.fromString(
                    "50000000-0000-0000-0000-000000000001"
            );

    static final UUID VISIT_NORTH_ID =
            UUID.fromString(
                    "50000000-0000-0000-0000-000000000002"
            );

    private final VisitMaintenanceActivityRepository
            visitMaintenanceActivityRepository;

    private final VisitRepository
            visitRepository;

    private final PoolMaintenanceActivityRepository
            poolMaintenanceActivityRepository;

    private final CrewMembershipRepository
            crewMembershipRepository;

    private final CrewRepository
            crewRepository;

    private final EmployeeRepository
            employeeRepository;

    private final MaintenanceActivityRepository
            maintenanceActivityRepository;

    private final SwimmingPoolRepository
            swimmingPoolRepository;

    private final Clock
            clock;

    public DataSeederDev(
            VisitMaintenanceActivityRepository visitMaintenanceActivityRepository,
            VisitRepository visitRepository,
            PoolMaintenanceActivityRepository poolMaintenanceActivityRepository,
            CrewMembershipRepository crewMembershipRepository,
            CrewRepository crewRepository,
            EmployeeRepository employeeRepository,
            MaintenanceActivityRepository maintenanceActivityRepository,
            SwimmingPoolRepository swimmingPoolRepository,
            Clock clock
    ) {

        this.visitMaintenanceActivityRepository =
                visitMaintenanceActivityRepository;

        this.visitRepository =
                visitRepository;

        this.poolMaintenanceActivityRepository =
                poolMaintenanceActivityRepository;

        this.crewMembershipRepository =
                crewMembershipRepository;

        this.crewRepository =
                crewRepository;

        this.employeeRepository =
                employeeRepository;

        this.maintenanceActivityRepository =
                maintenanceActivityRepository;

        this.swimmingPoolRepository =
                swimmingPoolRepository;

        this.clock =
                clock;
    }

    @Override
    @Transactional
    public void run(
            ApplicationArguments args
    ) {

        resetOperationalData();
        seedOperationalData();
    }

    private void resetOperationalData() {

        /*
         * Bulk deletes are intentional here.
         *
         * The DEV seeder recreates the canonical dataset with the same
         * deterministic UUIDs during the same startup transaction.
         * Using deleteAllInBatch() avoids loading deleted entities into
         * the persistence context before those identifiers are inserted again.
         *
         * The order follows the current operational dependency graph:
         * visit selections -> visits -> pool configuration ->
         * crew memberships -> crews -> employees/activities/pools.
         */
        visitMaintenanceActivityRepository
                .deleteAllInBatch();

        visitRepository
                .deleteAllInBatch();

        poolMaintenanceActivityRepository
                .deleteAllInBatch();

        crewMembershipRepository
                .deleteAllInBatch();

        crewRepository
                .deleteAllInBatch();

        employeeRepository
                .deleteAllInBatch();

        maintenanceActivityRepository
                .deleteAllInBatch();

        swimmingPoolRepository
                .deleteAllInBatch();
    }

    private void seedOperationalData() {

        SwimmingPool centralPool =
                new SwimmingPool(
                        POOL_CENTRAL_ID,
                        "Piscina Centro",
                        "Calle Mayor 1, Madrid"
                );

        SwimmingPool northPool =
                new SwimmingPool(
                        POOL_NORTH_ID,
                        "Piscina Norte",
                        "Calle de Bravo Murillo 100, Madrid"
                );

        swimmingPoolRepository
                .saveAll(
                        List.of(
                                centralPool,
                                northPool
                        )
                );

        MaintenanceActivity filterInspection =
                new MaintenanceActivity(
                        ACTIVITY_FILTER_INSPECTION_ID,
                        "Filter inspection",
                        "Check filter condition"
                );

        MaintenanceActivity waterQuality =
                new MaintenanceActivity(
                        ACTIVITY_WATER_QUALITY_ID,
                        "Water quality check",
                        "Check water quality parameters"
                );

        MaintenanceActivity surfaceCleaning =
                new MaintenanceActivity(
                        ACTIVITY_SURFACE_CLEANING_ID,
                        "Surface cleaning",
                        "Clean the pool surface and surrounding operational area"
                );

        MaintenanceActivity pumpInspection =
                new MaintenanceActivity(
                        ACTIVITY_PUMP_INSPECTION_ID,
                        "Pump inspection",
                        "Inspect pump operation and visible condition"
                );

        maintenanceActivityRepository
                .saveAll(
                        List.of(
                                filterInspection,
                                waterQuality,
                                surfaceCleaning,
                                pumpInspection
                        )
                );

        poolMaintenanceActivityRepository
                .saveAll(
                        List.of(
                                poolActivity(
                                        "61000000-0000-0000-0000-000000000001",
                                        POOL_CENTRAL_ID,
                                        ACTIVITY_FILTER_INSPECTION_ID
                                ),
                                poolActivity(
                                        "61000000-0000-0000-0000-000000000002",
                                        POOL_CENTRAL_ID,
                                        ACTIVITY_WATER_QUALITY_ID
                                ),
                                poolActivity(
                                        "61000000-0000-0000-0000-000000000003",
                                        POOL_CENTRAL_ID,
                                        ACTIVITY_SURFACE_CLEANING_ID
                                ),
                                poolActivity(
                                        "61000000-0000-0000-0000-000000000004",
                                        POOL_NORTH_ID,
                                        ACTIVITY_FILTER_INSPECTION_ID
                                ),
                                poolActivity(
                                        "61000000-0000-0000-0000-000000000005",
                                        POOL_NORTH_ID,
                                        ACTIVITY_WATER_QUALITY_ID
                                ),
                                poolActivity(
                                        "61000000-0000-0000-0000-000000000006",
                                        POOL_NORTH_ID,
                                        ACTIVITY_PUMP_INSPECTION_ID
                                )
                        )
                );

        Employee ana =
                new Employee(
                        EMPLOYEE_ANA_ID,
                        "Ana",
                        "Martinez"
                );

        Employee carlos =
                new Employee(
                        EMPLOYEE_CARLOS_ID,
                        "Carlos",
                        "Lopez"
                );

        Employee lucia =
                new Employee(
                        EMPLOYEE_LUCIA_ID,
                        "Lucia",
                        "Garcia"
                );

        Employee pedro =
                new Employee(
                        EMPLOYEE_PEDRO_ID,
                        "Pedro",
                        "Sanchez"
                );

        employeeRepository
                .saveAll(
                        List.of(
                                ana,
                                carlos,
                                lucia,
                                pedro
                        )
                );

        Crew morningCrew =
                new Crew(
                        CREW_MORNING_ID,
                        "Morning Crew"
                );

        morningCrew.assignSupervisor(
                EMPLOYEE_ANA_ID
        );

        Crew afternoonCrew =
                new Crew(
                        CREW_AFTERNOON_ID,
                        "Afternoon Crew"
                );

        afternoonCrew.assignSupervisor(
                EMPLOYEE_LUCIA_ID
        );

        crewRepository
                .saveAll(
                        List.of(
                                morningCrew,
                                afternoonCrew
                        )
                );

        crewMembershipRepository
                .saveAll(
                        List.of(
                                crewMembership(
                                        "62000000-0000-0000-0000-000000000001",
                                        CREW_MORNING_ID,
                                        EMPLOYEE_ANA_ID
                                ),
                                crewMembership(
                                        "62000000-0000-0000-0000-000000000002",
                                        CREW_MORNING_ID,
                                        EMPLOYEE_CARLOS_ID
                                ),
                                crewMembership(
                                        "62000000-0000-0000-0000-000000000003",
                                        CREW_AFTERNOON_ID,
                                        EMPLOYEE_LUCIA_ID
                                ),
                                crewMembership(
                                        "62000000-0000-0000-0000-000000000004",
                                        CREW_AFTERNOON_ID,
                                        EMPLOYEE_PEDRO_ID
                                )
                        )
                );

        LocalDate today =
                LocalDate.now(
                        clock
                );

        Visit centralVisit =
                new Visit(
                        VISIT_CENTRAL_ID,
                        POOL_CENTRAL_ID,
                        CREW_MORNING_ID,
                        today.plusDays(
                                1
                        ),
                        LocalTime.of(
                                9,
                                30
                        ),
                        "Use side entrance."
                );

        Visit northVisit =
                new Visit(
                        VISIT_NORTH_ID,
                        POOL_NORTH_ID,
                        CREW_AFTERNOON_ID,
                        today.plusDays(
                                2
                        ),
                        LocalTime.of(
                                11,
                                0
                        ),
                        "Check pump room before water-quality inspection."
                );

        visitRepository
                .saveAll(
                        List.of(
                                centralVisit,
                                northVisit
                        )
                );

        visitMaintenanceActivityRepository
                .saveAll(
                        List.of(
                                visitActivity(
                                        "63000000-0000-0000-0000-000000000001",
                                        VISIT_CENTRAL_ID,
                                        ACTIVITY_FILTER_INSPECTION_ID
                                ),
                                visitActivity(
                                        "63000000-0000-0000-0000-000000000002",
                                        VISIT_CENTRAL_ID,
                                        ACTIVITY_WATER_QUALITY_ID
                                ),
                                visitActivity(
                                        "63000000-0000-0000-0000-000000000003",
                                        VISIT_NORTH_ID,
                                        ACTIVITY_WATER_QUALITY_ID
                                ),
                                visitActivity(
                                        "63000000-0000-0000-0000-000000000004",
                                        VISIT_NORTH_ID,
                                        ACTIVITY_PUMP_INSPECTION_ID
                                )
                        )
                );
    }

    private PoolMaintenanceActivity poolActivity(
            String id,
            UUID poolId,
            UUID activityId
    ) {

        return new PoolMaintenanceActivity(
                UUID.fromString(
                        id
                ),
                poolId,
                activityId
        );
    }

    private CrewMembership crewMembership(
            String id,
            UUID crewId,
            UUID employeeId
    ) {

        return new CrewMembership(
                UUID.fromString(
                        id
                ),
                crewId,
                employeeId
        );
    }

    private VisitMaintenanceActivity visitActivity(
            String id,
            UUID visitId,
            UUID activityId
    ) {

        return new VisitMaintenanceActivity(
                UUID.fromString(
                        id
                ),
                visitId,
                activityId
        );
    }
}
