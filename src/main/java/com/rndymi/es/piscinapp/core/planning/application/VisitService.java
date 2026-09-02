package com.rndymi.es.piscinapp.core.planning.application;

import com.rndymi.es.piscinapp.core.crews.application.CrewLookup;
import com.rndymi.es.piscinapp.core.crews.application.CrewReference;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeLookup;
import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeNotFoundException;
import com.rndymi.es.piscinapp.core.maintenance.application.MaintenanceActivityReference;
import com.rndymi.es.piscinapp.core.maintenance.application.MaintenancePlanningLookup;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitActivityNotApplicableException;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitCrewNotAssignableException;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitInvalidScheduleException;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitNotFoundException;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitStateConflictException;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.domain.VisitMaintenanceActivity;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitRepository;
import com.rndymi.es.piscinapp.core.planning.persistence.VisitSpecifications;
import com.rndymi.es.piscinapp.core.platform.application.InactiveResourceException;
import com.rndymi.es.piscinapp.core.pools.application.PoolLookup;
import com.rndymi.es.piscinapp.core.pools.application.PoolReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VisitService
        implements VisitLookup {

    private final VisitRepository
            visitRepository;

    private final VisitMaintenanceActivityRepository
            visitMaintenanceActivityRepository;

    private final PoolLookup
            poolLookup;

    private final CrewLookup
            crewLookup;

    private final EmployeeLookup
            employeeLookup;

    private final MaintenancePlanningLookup
            maintenancePlanningLookup;

    private final Clock
            clock;

    public VisitService(
            VisitRepository visitRepository,
            VisitMaintenanceActivityRepository visitMaintenanceActivityRepository,
            PoolLookup poolLookup,
            CrewLookup crewLookup,
            EmployeeLookup employeeLookup,
            MaintenancePlanningLookup maintenancePlanningLookup,
            Clock clock
    ) {

        this.visitRepository =
                visitRepository;

        this.visitMaintenanceActivityRepository =
                visitMaintenanceActivityRepository;

        this.poolLookup =
                poolLookup;

        this.crewLookup =
                crewLookup;

        this.employeeLookup =
                employeeLookup;

        this.maintenancePlanningLookup =
                maintenancePlanningLookup;

        this.clock =
                clock;
    }

    @Transactional
    public Visit createVisit(
            UUID poolId,
            UUID crewId,
            LocalDate plannedDate,
            LocalTime plannedTime,
            Collection<UUID> maintenanceActivityIds,
            String notes
    ) {

        List<UUID> validatedActivityIds =
                validatePlanning(
                        poolId,
                        crewId,
                        plannedDate,
                        plannedTime,
                        maintenanceActivityIds
                );

        Visit visit =
                visitRepository
                        .saveAndFlush(
                                new Visit(
                                        UUID.randomUUID(),
                                        poolId,
                                        crewId,
                                        plannedDate,
                                        plannedTime,
                                        normalizeNotes(
                                                notes
                                        )
                                )
                        );

        saveActivities(
                visit.getId(),
                validatedActivityIds
        );

        return visit;
    }

    @Transactional(readOnly = true)
    public Visit getVisit(
            UUID visitId
    ) {

        return requireVisitEntity(
                visitId
        );
    }

    @Transactional(readOnly = true)
    public Page<Visit> listVisits(
            VisitSearchCriteria criteria,
            Pageable pageable
    ) {

        validateCriteria(
                criteria
        );

        return visitRepository
                .findAll(
                        VisitSpecifications.from(
                                criteria
                        ),
                        pageable
                );
    }

    @Transactional
    public Visit updateVisit(
            UUID visitId,
            UUID poolId,
            UUID crewId,
            LocalDate plannedDate,
            LocalTime plannedTime,
            Collection<UUID> maintenanceActivityIds,
            String notes
    ) {

        Visit visit =
                requireEditableVisit(
                        visitId
                );

        List<UUID> validatedActivityIds =
                validatePlanning(
                        poolId,
                        crewId,
                        plannedDate,
                        plannedTime,
                        maintenanceActivityIds
                );

        visit.updatePlanning(
                poolId,
                crewId,
                plannedDate,
                plannedTime,
                normalizeNotes(
                        notes
                )
        );

        replaceActivities(
                visitId,
                validatedActivityIds
        );

        return visit;
    }

    @Transactional
    public Visit cancelVisit(
            UUID visitId
    ) {

        Visit visit =
                requireEditableVisit(
                        visitId
                );

        visit.cancel();

        return visit;
    }

    @Transactional(readOnly = true)
    public List<UUID> getActivityIds(
            UUID visitId
    ) {

        requireVisitEntity(
                visitId
        );

        return toDeterministicActivityIds(
                visitMaintenanceActivityRepository
                        .findAllByVisitId(
                                visitId
                        )
        );
    }

    @Transactional(readOnly = true)
    public Map<UUID, List<UUID>>
    getActivityIdsByVisitIds(
            Collection<UUID> visitIds
    ) {

        if (visitIds.isEmpty()) {
            return Map.of();
        }

        return visitMaintenanceActivityRepository
                .findAllByVisitIdIn(
                        visitIds
                )
                .stream()
                .collect(
                        Collectors.groupingBy(
                                VisitMaintenanceActivity::getVisitId,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        this::toDeterministicActivityIds
                                )
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public VisitReference requireVisit(
            UUID visitId
    ) {

        Visit visit =
                requireVisitEntity(
                        visitId
                );

        return new VisitReference(
                visit.getId(),
                visit.getPoolId(),
                visit.getCrewId(),
                visit.getStatus(),
                Set.copyOf(
                        getActivityIds(
                                visitId
                        )
                )
        );
    }

    private List<UUID> validatePlanning(
            UUID poolId,
            UUID crewId,
            LocalDate plannedDate,
            LocalTime plannedTime,
            Collection<UUID> maintenanceActivityIds
    ) {

        validateSchedule(
                plannedDate,
                plannedTime
        );

        validatePool(
                poolId
        );

        validateCrew(
                crewId
        );

        return validateActivities(
                poolId,
                maintenanceActivityIds
        );
    }

    private void validatePool(
            UUID poolId
    ) {

        PoolReference pool =
                poolLookup
                        .requirePool(
                                poolId
                        );

        if (!pool.active()) {

            throw new InactiveResourceException(
                    "Swimming pool",
                    poolId
            );
        }
    }

    private void validateCrew(
            UUID crewId
    ) {

        CrewReference crew =
                crewLookup
                        .requireCrew(
                                crewId
                        );

        if (
                !crew.active()
                        ||
                        crew.memberIds() == null
                        ||
                        crew.memberIds().isEmpty()
                        ||
                        crew.supervisorEmployeeId() == null
                        ||
                        !crew.memberIds().contains(
                                crew.supervisorEmployeeId()
                        )
        ) {

            throw new VisitCrewNotAssignableException(
                    crewId
            );
        }

        try {

            for (
                    UUID memberId
                    :
                    crew.memberIds()
            ) {

                if (
                        !employeeLookup
                                .requireEmployee(
                                        memberId
                                )
                                .active()
                ) {

                    throw new VisitCrewNotAssignableException(
                            crewId
                    );
                }
            }

        } catch (
                EmployeeNotFoundException exception
        ) {

            throw new VisitCrewNotAssignableException(
                    crewId
            );
        }
    }

    private List<UUID> validateActivities(
            UUID poolId,
            Collection<UUID> maintenanceActivityIds
    ) {

        if (
                maintenanceActivityIds == null
                        ||
                        maintenanceActivityIds.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "At least one maintenance activity is required"
            );
        }

        LinkedHashSet<UUID> uniqueActivityIds =
                new LinkedHashSet<>(
                        maintenanceActivityIds
                );

        if (
                uniqueActivityIds.size()
                        != maintenanceActivityIds.size()
        ) {

            throw new IllegalArgumentException(
                    "Maintenance activity IDs must be unique"
            );
        }

        for (
                UUID activityId
                :
                uniqueActivityIds
        ) {

            if (activityId == null) {

                throw new IllegalArgumentException(
                        "Maintenance activity ID must not be null"
                );
            }

            MaintenanceActivityReference activity =
                    maintenancePlanningLookup
                            .requireActivity(
                                    activityId
                            );

            if (!activity.active()) {

                throw new InactiveResourceException(
                        "Maintenance activity",
                        activityId
                );
            }

            if (
                    !maintenancePlanningLookup
                            .isApplicable(
                                    poolId,
                                    activityId
                            )
            ) {

                throw new VisitActivityNotApplicableException(
                        poolId,
                        activityId
                );
            }
        }

        return uniqueActivityIds
                .stream()
                .toList();
    }

    private void validateSchedule(
            LocalDate plannedDate,
            LocalTime plannedTime
    ) {

        LocalDateTime planned =
                LocalDateTime.of(
                        plannedDate,
                        plannedTime
                );

        if (
                planned.isBefore(
                        LocalDateTime.now(
                                clock
                        )
                )
        ) {

            throw new VisitInvalidScheduleException(
                    plannedDate,
                    plannedTime
            );
        }
    }

    private void validateCriteria(
            VisitSearchCriteria criteria
    ) {

        if (
                criteria.date() != null
                        &&
                        (
                                criteria.fromDate() != null
                                        ||
                                        criteria.toDate() != null
                        )
        ) {

            throw new IllegalArgumentException(
                    "date cannot be combined with fromDate or toDate"
            );
        }

        if (
                criteria.fromDate() != null
                        &&
                        criteria.toDate() != null
                        &&
                        criteria
                                .fromDate()
                                .isAfter(
                                        criteria.toDate()
                                )
        ) {

            throw new IllegalArgumentException(
                    "fromDate must not be after toDate"
            );
        }
    }

    private Visit requireEditableVisit(
            UUID visitId
    ) {

        Visit visit =
                requireVisitEntity(
                        visitId
                );

        if (
                visit.getStatus()
                        != VisitStatus.PLANNED
        ) {

            throw new VisitStateConflictException(
                    visitId,
                    visit.getStatus()
            );
        }

        return visit;
    }

    private Visit requireVisitEntity(
            UUID visitId
    ) {

        return visitRepository
                .findById(
                        visitId
                )
                .orElseThrow(
                        () ->
                                new VisitNotFoundException(
                                        visitId
                                )
                );
    }

    private void replaceActivities(
            UUID visitId,
            Collection<UUID> maintenanceActivityIds
    ) {

        visitMaintenanceActivityRepository
                .deleteAllByVisitId(
                        visitId
                );

        visitMaintenanceActivityRepository
                .flush();

        saveActivities(
                visitId,
                maintenanceActivityIds
        );
    }

    private void saveActivities(
            UUID visitId,
            Collection<UUID> maintenanceActivityIds
    ) {

        visitMaintenanceActivityRepository
                .saveAllAndFlush(
                        maintenanceActivityIds
                                .stream()
                                .map(
                                        activityId ->
                                                new VisitMaintenanceActivity(
                                                        UUID.randomUUID(),
                                                        visitId,
                                                        activityId
                                                )
                                )
                                .toList()
                );
    }

    private List<UUID> toDeterministicActivityIds(
            Collection<VisitMaintenanceActivity> activities
    ) {

        return activities
                .stream()
                .map(
                        VisitMaintenanceActivity::getMaintenanceActivityId
                )
                .sorted(
                        Comparator.comparing(
                                UUID::toString
                        )
                )
                .toList();
    }

    private String normalizeNotes(
            String notes
    ) {

        if (
                notes == null
                        ||
                        notes.isBlank()
        ) {

            return null;
        }

        return notes.strip();
    }
}
