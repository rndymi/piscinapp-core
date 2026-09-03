package com.rndymi.es.piscinapp.core.crews.persistence;

import com.rndymi.es.piscinapp.core.crews.domain.CrewMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrewMembershipRepository
        extends JpaRepository<CrewMembership, UUID> {

    boolean existsByCrewIdAndEmployeeId(
            UUID crewId,
            UUID employeeId
    );

    Optional<CrewMembership>
    findByCrewIdAndEmployeeId(
            UUID crewId,
            UUID employeeId
    );

    List<CrewMembership>
    findAllByCrewId(
            UUID crewId
    );

    List<CrewMembership>
    findAllByCrewIdIn(
            Collection<UUID> crewIds
    );

    List<CrewMembership> findAllByEmployeeId(
            UUID employeeId
    );
}