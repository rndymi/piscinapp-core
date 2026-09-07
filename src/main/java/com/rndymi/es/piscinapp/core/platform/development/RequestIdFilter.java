package com.rndymi.es.piscinapp.core.platform.development;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Profile("dev")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter
        extends OncePerRequestFilter {

    static final String HEADER_NAME =
            "X-Request-Id";

    static final String MDC_KEY =
            "requestId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException,
            IOException {

        String requestId =
                resolveRequestId(
                        request
                );

        MDC.put(
                MDC_KEY,
                requestId
        );

        response.setHeader(
                HEADER_NAME,
                requestId
        );

        try {

            filterChain.doFilter(
                    request,
                    response
            );
        }
        finally {

            MDC.remove(
                    MDC_KEY
            );
        }
    }

    private String resolveRequestId(
            HttpServletRequest request
    ) {

        String incomingRequestId =
                request.getHeader(
                        HEADER_NAME
                );

        if (
                incomingRequestId != null
                        && !incomingRequestId.isBlank()
                        && incomingRequestId.length() <= 128
        ) {

            return incomingRequestId;
        }

        return UUID.randomUUID()
                .toString();
    }
}
