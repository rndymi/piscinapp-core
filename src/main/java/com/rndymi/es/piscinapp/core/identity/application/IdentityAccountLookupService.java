package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.application.exception.UserAccountNotFoundException;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IdentityAccountLookupService
        implements IdentityAccountLookup {

    private final UserAccountRepository
            userAccountRepository;

    public IdentityAccountLookupService(
            UserAccountRepository userAccountRepository
    ) {

        this.userAccountRepository =
                userAccountRepository;
    }

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
}
