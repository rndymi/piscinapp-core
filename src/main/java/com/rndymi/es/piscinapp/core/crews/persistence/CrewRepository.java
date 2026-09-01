package com.rndymi.es.piscinapp.core.crews.persistence;

import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CrewRepository
        extends JpaRepository<Crew, UUID>,
        JpaSpecificationExecutor<Crew> {
}
