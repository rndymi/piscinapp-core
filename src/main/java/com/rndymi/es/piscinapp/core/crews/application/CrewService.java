package com.rndymi.es.piscinapp.core.crews.application;

import com.rndymi.es.piscinapp.core.crews.application.exception.CrewMemberNotFoundException;
import com.rndymi.es.piscinapp.core.crews.application.exception.CrewMembershipConflictException;
import com.rndymi.es.piscinapp.core.crews.application.exception.CrewNotFoundException;
import com.rndymi.es.piscinapp.core.crews.application.exception.CrewSupervisorConflictException;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.domain.CrewMembership;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewSpecifications;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeLookup;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeReference;
import com.rndymi.es.piscinapp.core.platform.application.InactiveResourceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CrewService {

    private static final String
            MEMBERSHIP_CONSTRAINT =
            "uk_crew_memberships_crew_employee";

    private final CrewRepository
            crewRepository;

    private final CrewMembershipRepository
            crewMembershipRepository;

    private final EmployeeLookup
            employeeLookup;

    public CrewService(
            CrewRepository crewRepository,
            CrewMembershipRepository crewMembershipRepository,
            EmployeeLookup employeeLookup
    ) {

        this.crewRepository =
                crewRepository;

        this.crewMembershipRepository =
                crewMembershipRepository;

        this.employeeLookup =
                employeeLookup;
    }

    @Transactional
    public Crew createCrew(
            String name
    ) {

        Crew crew =
                new Crew(
                        UUID.randomUUID(),
                        normalizeName(
                                name
                        )
                );

        return crewRepository
                .saveAndFlush(
                        crew
                );
    }

    @Transactional(readOnly = true)
    public Crew getCrew(
            UUID crewId
    ) {

        return crewRepository
                .findById(
                        crewId
                )
                .orElseThrow(
                        () ->
                                new CrewNotFoundException(
                                        crewId
                                )
                );
    }

    @Transactional(readOnly = true)
    public Page<Crew> listCrews(
            Boolean active,
            String search,
            Pageable pageable
    ) {

        Specification<Crew>
                specification =
                CrewSpecifications
                        .activeEquals(
                                active
                        )
                        .and(
                                CrewSpecifications
                                        .nameContains(
                                                search
                                        )
                        );

        return crewRepository
                .findAll(
                        specification,
                        pageable
                );
    }

    @Transactional
    public Crew updateCrew(
            UUID crewId,
            String name
    ) {

        Crew crew =
                getCrewForUpdate(
                        crewId
                );

        crew.rename(
                normalizeName(
                        name
                )
        );

        return crew;
    }

    @Transactional
    public Crew updateStatus(
            UUID crewId,
            boolean active
    ) {

        Crew crew =
                getCrewForUpdate(
                        crewId
                );

        if (active) {

            crew.activate();

        } else {

            crew.deactivate();
        }

        return crew;
    }

    @Transactional
    public void addMember(
            UUID crewId,
            UUID employeeId
    ) {

        requireCrew(
                crewId
        );

        EmployeeReference employee =
                employeeLookup
                        .requireEmployee(
                                employeeId
                        );

        requireActiveEmployee(
                employee
        );

        if (
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crewId,
                                employeeId
                        )
        ) {

            throw new CrewMembershipConflictException(
                    crewId,
                    employeeId
            );
        }

        try {

            crewMembershipRepository
                    .saveAndFlush(
                            new CrewMembership(
                                    UUID.randomUUID(),
                                    crewId,
                                    employeeId
                            )
                    );

        } catch (
                DataIntegrityViolationException
                        exception
        ) {

            if (
                    isMembershipConstraintViolation(
                            exception
                    )
            ) {

                throw new CrewMembershipConflictException(
                        crewId,
                        employeeId
                );
            }

            throw exception;
        }
    }

    @Transactional
    public void removeMember(
            UUID crewId,
            UUID employeeId
    ) {

        Crew crew =
                getCrewForUpdate(
                        crewId
                );

        CrewMembership membership =
                crewMembershipRepository
                        .findByCrewIdAndEmployeeId(
                                crewId,
                                employeeId
                        )
                        .orElseThrow(
                                () ->
                                        new CrewMemberNotFoundException(
                                                crewId,
                                                employeeId
                                        )
                        );

        if (
                employeeId.equals(
                        crew.getSupervisorEmployeeId()
                )
        ) {

            throw new CrewSupervisorConflictException(
                    "Current crew supervisor cannot be removed as member. "
                            + "Change or clear the supervisor first."
            );
        }

        crewMembershipRepository
                .delete(
                        membership
                );
    }

    @Transactional
    public void assignSupervisor(
            UUID crewId,
            UUID employeeId
    ) {

        Crew crew =
                getCrewForUpdate(
                        crewId
                );

        EmployeeReference employee =
                employeeLookup
                        .requireEmployee(
                                employeeId
                        );

        requireActiveEmployee(
                employee
        );

        if (
                !crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crewId,
                                employeeId
                        )
        ) {

            throw new CrewSupervisorConflictException(
                    "Crew supervisor must already be a member of the crew"
            );
        }

        crew.assignSupervisor(
                employeeId
        );
    }

    @Transactional
    public void clearSupervisor(
            UUID crewId
    ) {

        Crew crew =
                getCrewForUpdate(
                        crewId
                );

        crew.clearSupervisor();
    }

    @Transactional(readOnly = true)
    public List<UUID> getMemberIds(
            UUID crewId
    ) {

        requireCrew(
                crewId
        );

        return crewMembershipRepository
                .findAllByCrewId(
                        crewId
                )
                .stream()
                .map(
                        CrewMembership::getEmployeeId
                )
                .sorted(
                        Comparator
                                .comparing(
                                        UUID::toString
                                )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<UUID, List<UUID>>
    getMemberIdsByCrewIds(
            Collection<UUID> crewIds
    ) {

        if (crewIds.isEmpty()) {

            return Map.of();
        }

        return crewMembershipRepository
                .findAllByCrewIdIn(
                        crewIds
                )
                .stream()
                .collect(
                        Collectors.groupingBy(
                                CrewMembership::getCrewId,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(
                                                CrewMembership::getEmployeeId,
                                                Collectors.toList()
                                        ),
                                        memberIds ->
                                                memberIds
                                                        .stream()
                                                        .sorted(
                                                                Comparator
                                                                        .comparing(
                                                                                UUID::toString
                                                                        )
                                                        )
                                                        .toList()
                                )
                        )
                );
    }

    private Crew getCrewForUpdate(
            UUID crewId
    ) {

        return requireCrew(
                crewId
        );
    }

    private Crew requireCrew(
            UUID crewId
    ) {

        return crewRepository
                .findById(
                        crewId
                )
                .orElseThrow(
                        () ->
                                new CrewNotFoundException(
                                        crewId
                                )
                );
    }

    private void requireActiveEmployee(
            EmployeeReference employee
    ) {

        if (!employee.active()) {

            throw new InactiveResourceException(
                    "Employee",
                    employee.id()
            );
        }
    }

    private String normalizeName(
            String value
    ) {

        return value.strip();
    }

    private boolean
    isMembershipConstraintViolation(
            DataIntegrityViolationException exception
    ) {

        Throwable current =
                exception;

        while (current != null) {

            String message =
                    current.getMessage();

            if (
                    message != null
                            &&
                            message.contains(
                                    MEMBERSHIP_CONSTRAINT
                            )
            ) {

                return true;
            }

            current =
                    current.getCause();
        }

        return false;
    }
}
