package com.rndymi.es.piscinapp.core.identity.bootstrap;

import com.rndymi.es.piscinapp.core.identity.application.UserAccountService;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(0)
public class InitialOwnerBootstrap
        implements ApplicationRunner {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountService userAccountService;
    private final BootstrapOwnerProperties properties;

    @Override
    public void run(
            ApplicationArguments args
    ) {

        if (
                userAccountRepository
                        .existsByOwnerTrue()
        ) {

            return;
        }

        try {

            userAccountService
                    .createOwnerAccount(
                            properties.getUsername(),
                            properties.getPassword()
                    );

        } catch (
                RuntimeException exception
        ) {

            throw new IllegalStateException(
                    "Valid bootstrap Owner credentials are required when no protected Owner exists",
                    exception
            );
        }
    }
}
