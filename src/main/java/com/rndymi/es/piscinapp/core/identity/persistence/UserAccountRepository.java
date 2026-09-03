package com.rndymi.es.piscinapp.core.identity.persistence;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, UUID> {

    @EntityGraph(
            attributePaths = "roles"
    )
    Optional<UserAccount> findByUsername(
            String username
    );

    boolean existsByUsername(
            String username
    );

    boolean existsByOwnerTrue();

    @Query("""
            select case
                       when count(account) > 0
                       then true
                       else false
                   end
            from UserAccount account
            join account.roles role
            where role = :role
            """)
    boolean existsByRole(
            @Param("role")
            SecurityRole role
    );

    @EntityGraph(
            attributePaths = "roles"
    )
    List<UserAccount>
    findAllByOrderByUsernameAsc();

    @EntityGraph(
            attributePaths = "roles"
    )
    @Query("""
            select account
            from UserAccount account
            where account.id = :id
            """)
    Optional<UserAccount> findWithRolesById(
            @Param("id")
            UUID id
    );

    @EntityGraph(
            attributePaths = "roles"
    )
    List<UserAccount> findAllByOwnerFalse();

    @Lock(
            LockModeType.PESSIMISTIC_WRITE
    )
    @Query("""
            select distinct account
            from UserAccount account
            join account.roles role
            where account.enabled = true
              and role = :role
            """)
    List<UserAccount>
    findEnabledAccountsByRoleForUpdate(
            @Param("role")
            SecurityRole role
    );
}
