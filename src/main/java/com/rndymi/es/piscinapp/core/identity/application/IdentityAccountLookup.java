package com.rndymi.es.piscinapp.core.identity.application;

import java.util.UUID;

public interface IdentityAccountLookup {

    void requireExistingAccount(
            UUID accountId
    );
}
