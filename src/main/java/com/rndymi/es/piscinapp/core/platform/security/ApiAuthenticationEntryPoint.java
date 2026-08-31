package com.rndymi.es.piscinapp.core.platform.security;

import com.rndymi.es.piscinapp.core.platform.web.ApiErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;

@Component
public class ApiAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final JsonMapper
            jsonMapper;

    public ApiAuthenticationEntryPoint(
            JsonMapper jsonMapper
    ) {

        this.jsonMapper =
                jsonMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException
                    authenticationException
    ) throws IOException {

        ProblemDetail detail =
                ProblemDetail
                        .forStatusAndDetail(
                                HttpStatus.UNAUTHORIZED,
                                "Authentication is required"
                        );

        detail.setTitle(
                HttpStatus.UNAUTHORIZED
                        .getReasonPhrase()
        );

        detail.setInstance(
                URI.create(
                        request.getRequestURI()
                )
        );

        detail.setProperty(
                "code",
                ApiErrorCode
                        .AUTHENTICATION_REQUIRED
                        .name()
        );

        response.setStatus(
                HttpStatus.UNAUTHORIZED
                        .value()
        );

        response.setHeader(
                HttpHeaders.WWW_AUTHENTICATE,
                "Bearer"
        );

        response.setContentType(
                MediaType
                        .APPLICATION_PROBLEM_JSON_VALUE
        );

        jsonMapper.writeValue(
                response.getOutputStream(),
                detail
        );
    }
}
