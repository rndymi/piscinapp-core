package com.rndymi.es.piscinapp.core.planning.persistence;

import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface VisitRepository
        extends JpaRepository<Visit, UUID>,
        JpaSpecificationExecutor<Visit> {
}
