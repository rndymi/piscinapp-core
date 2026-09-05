package com.rndymi.es.piscinapp.core.incidents.persistence;

import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface IncidentRepository
        extends JpaRepository<Incident, UUID>,
        JpaSpecificationExecutor<Incident> {

    List<Incident>
    findAllByVisitIdOrderByCreatedAtAscIdAsc(
            UUID visitId
    );
}
