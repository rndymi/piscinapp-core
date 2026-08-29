package com.rndymi.es.piscinapp.core.identity.security;

import com.rndymi.es.piscinapp.core.identity.application.UsernameNormalizer;
import com.rndymi.es.piscinapp.core.identity.persistence.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountDetailsService
        implements UserDetailsService {

    private final UserAccountRepository
            userAccountRepository;

    public UserAccountDetailsService(
            UserAccountRepository userAccountRepository
    ) {

        this.userAccountRepository =
                userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            String username
    )
            throws UsernameNotFoundException {

        String normalizedUsername =
                UsernameNormalizer.normalize(
                        username
                );

        if (
                normalizedUsername == null
                        || normalizedUsername.isBlank()
        ) {

            throw new UsernameNotFoundException(
                    "User account was not found"
            );
        }

        return userAccountRepository
                .findByUsername(
                        normalizedUsername
                )
                .map(
                        PiscinAppUserDetails::new
                )
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        "User account was not found"
                                )
                );
    }
}
