package com.rndymi.es.piscinapp.core.platform.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

        private static final String
                SECURITY_SCHEME =
                "piscinappOAuth2";

        @Bean
        OpenAPI piscinAppOpenApi(
                @Value("${piscinapp.security.issuer}")
                String issuer
        ) {

                OAuthFlow authorizationCode =
                        new OAuthFlow()
                                .authorizationUrl(
                                        issuer
                                                + "/oauth2/authorize"
                                )
                                .tokenUrl(
                                        issuer
                                                + "/oauth2/token"
                                )
                                .scopes(
                                        new Scopes()
                                                .addString(
                                                        "openid",
                                                        "OpenID Connect"
                                                )
                                                .addString(
                                                        "profile",
                                                        "Basic identity profile"
                                                )
                                );

                SecurityScheme securityScheme =
                        new SecurityScheme()
                                .type(
                                        SecurityScheme.Type.OAUTH2
                                )
                                .flows(
                                        new OAuthFlows()
                                                .authorizationCode(
                                                        authorizationCode
                                                )
                                );

                return new OpenAPI()
                        .info(
                                new Info()
                                        .title(
                                                "PiscinApp Core API"
                                        )
                                        .version("v1")
                        )
                        .components(
                                new Components()
                                        .addSecuritySchemes(
                                                SECURITY_SCHEME,
                                                securityScheme
                                        )
                        )
                        .addSecurityItem(
                                new SecurityRequirement()
                                        .addList(
                                                SECURITY_SCHEME
                                        )
                        );
        }
}
