package com.rndymi.es.piscinapp.core.identity.application;

import com.rndymi.es.piscinapp.core.identity.application.exception.UserAccountNotFoundException;
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
}
