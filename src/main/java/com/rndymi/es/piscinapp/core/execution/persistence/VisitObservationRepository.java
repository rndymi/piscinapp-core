package com.rndymi.es.piscinapp.core.execution.persistence;

import com.rndymi.es.piscinapp.core.execution.domain.VisitObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitObservationRepository
        extends JpaRepository<VisitObservation, UUID> {

    List<VisitObservation>
    findAllByVisitIdOrderByCreatedAtAscIdAsc(
            UUID visitId
    );
}
