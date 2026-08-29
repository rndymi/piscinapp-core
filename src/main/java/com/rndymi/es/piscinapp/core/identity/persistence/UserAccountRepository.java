package com.rndymi.es.piscinapp.core.identity.persistence;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByUsername(
            String username
    );

    boolean existsByUsername(
            String username
    );

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
}
