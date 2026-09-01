package com.rndymi.es.piscinapp.core.pools.persistence;

import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SwimmingPoolRepository
        extends JpaRepository<SwimmingPool, UUID>,
        JpaSpecificationExecutor<SwimmingPool> {
}
