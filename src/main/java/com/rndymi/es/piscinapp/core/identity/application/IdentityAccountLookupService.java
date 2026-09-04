package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.application.exception.UserAccountNotFoundException;
import com.rndymi.es.piscinapp.core.identity.domain.UserAccount;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityAccountLookupService
        implements IdentityAccountLookup {

    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public void requireExistingAccount(
            UUID accountId
    ) {

        if (
                !userAccountRepository
                        .existsById(
                                accountId
                        )
        ) {

            throw new UserAccountNotFoundException(
                    accountId
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UUID requireAccountIdByPrincipalName(
            String principalName
    ) {

        return userAccountRepository
                .findByUsername(
                        principalName
                )
                .map(
                        UserAccount::getId
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Authenticated account could not be resolved"
                                )
                );
    }
}
