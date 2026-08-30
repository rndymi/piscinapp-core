package com.rndymi.es.piscinapp.core.platform.security;

import com.rndymi.es.piscinapp.core.identity.domain.SecurityRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;

@Configuration
public class JwtRoleClaimCustomizer {

    private static final String ROLE_PREFIX =
            "ROLE_";

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext>
    piscinAppRoleTokenCustomizer() {

        return context -> {

            if (
                    !OAuth2TokenType.ACCESS_TOKEN.equals(
                            context.getTokenType()
                    )
            ) {

                return;
            }

            List<String> roles =
                    context.getPrincipal()
                            .getAuthorities()
                            .stream()
                            .map(
                                    GrantedAuthority::getAuthority
                            )
                            .filter(
                                    authority ->
                                            authority.startsWith(
                                                    ROLE_PREFIX
                                            )
                            )
                            .map(
                                    authority ->
                                            authority.substring(
                                                    ROLE_PREFIX.length()
                                            )
                            )
                            .filter(
                                    JwtRoleClaimCustomizer
                                            ::isPiscinAppRole
                            )
                            .sorted()
                            .toList();

            context.getClaims()
                    .claim(
                            "roles",
                            roles
                    );
        };
    }

    private static boolean isPiscinAppRole(
            String role
    ) {

        try {

            SecurityRole.valueOf(role);

            return true;

        } catch (IllegalArgumentException exception) {

            return false;
        }
    }
}
