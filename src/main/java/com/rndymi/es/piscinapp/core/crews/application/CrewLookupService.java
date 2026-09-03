package com.rndymi.es.piscinapp.core.crews.application;

import com.rndymi.es.piscinapp.core.crews.application.exception.CrewNotFoundException;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.domain.CrewMembership;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrewLookupService
        implements CrewLookup {

    private final CrewRepository crewRepository;
    private final CrewMembershipRepository crewMembershipRepository;

    @Override
    @Transactional(readOnly = true)
    public CrewReference requireCrew(
            UUID crewId
    ) {

        Crew crew =
                crewRepository
                        .findById(
                                crewId
                        )
                        .orElseThrow(
                                () ->
                                        new CrewNotFoundException(
                                                crewId
                                        )
                        );

        Set<UUID> memberIds =
                crewMembershipRepository
                        .findAllByCrewId(
                                crewId
                        )
                        .stream()
                        .map(
                                CrewMembership::getEmployeeId
                        )
                        .collect(
                                Collectors.toUnmodifiableSet()
                        );

        return new CrewReference(
                crew.getId(),
                crew.isActive(),
                crew.getSupervisorEmployeeId(),
                memberIds
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> findCrewIdsByEmployeeId(
            UUID employeeId
    ) {

        return crewMembershipRepository
                .findAllByEmployeeId(
                        employeeId
                )
                .stream()
                .map(
                        CrewMembership::getCrewId
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }
}
