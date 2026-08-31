package com.rndymi.es.piscinapp.core.platform.security;

import com.rndymi.es.piscinapp.core.platform.web.ApiErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;

@Component
public class ApiAccessDeniedHandler
        implements AccessDeniedHandler {

    private final JsonMapper
            jsonMapper;

    public ApiAccessDeniedHandler(
            JsonMapper jsonMapper
    ) {

        this.jsonMapper =
                jsonMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException
                    accessDeniedException
    ) throws IOException {

        ProblemDetail detail =
                ProblemDetail
                        .forStatusAndDetail(
                                HttpStatus.FORBIDDEN,
                                "Access is denied"
                        );

        detail.setTitle(
                HttpStatus.FORBIDDEN
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
                        .ACCESS_DENIED
                        .name()
        );

        response.setStatus(
                HttpStatus.FORBIDDEN
                        .value()
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
