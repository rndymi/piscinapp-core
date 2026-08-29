package com.rndymi.es.piscinapp.core.identity.bootstrap;

import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class InitialAdminBootstrap
        implements ApplicationRunner {

    private final UserAccountRepository
            userAccountRepository;

    private final UserAccountService
            userAccountService;

    private final BootstrapAdminProperties
            properties;

    public InitialAdminBootstrap(
            UserAccountRepository userAccountRepository,
            UserAccountService userAccountService,
            BootstrapAdminProperties properties
    ) {

        this.userAccountRepository =
                userAccountRepository;

        this.userAccountService =
                userAccountService;

        this.properties =
                properties;
    }

    @Override
    public void run(
            ApplicationArguments args
    ) {

        if (
                userAccountRepository.existsByRole(
                        SecurityRole.ADMIN
                )
        ) {

            return;
        }

        try {

            userAccountService.createAccount(
                    properties.getUsername(),
                    properties.getPassword(),
                    true,
                    EnumSet.of(
                            SecurityRole.USER,
                            SecurityRole.ADMIN
                    )
            );

        } catch (
                IllegalArgumentException exception
        ) {

            throw new IllegalStateException(
                    "Valid bootstrap administrator credentials are required when no administrator exists",
                    exception
            );
        }
    }
}
