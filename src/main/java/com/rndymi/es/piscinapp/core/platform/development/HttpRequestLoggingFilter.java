package com.rndymi.es.piscinapp.core.platform.development;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@Profile("dev")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class HttpRequestLoggingFilter
        extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException,
            IOException {

        Instant start =
                Instant.now();

        try {

            filterChain.doFilter(
                    request,
                    response
            );
        }
        finally {

            long durationMillis =
                    Duration.between(
                                    start,
                                    Instant.now()
                            )
                            .toMillis();

            log.debug(
                    "HTTP [{}] {} {} -> {} ({} ms)",
                    MDC.get(
                            RequestIdFilter.MDC_KEY
                    ),
                    request.getMethod(),
                    sanitizePath(
                            request.getRequestURI()
                    ),
                    response.getStatus(),
                    durationMillis
            );
        }
    }

    private String sanitizePath(
            String requestUri
    ) {

        if (
                requestUri == null
                        || requestUri.isBlank()
        ) {

            return "/";
        }

        return requestUri;
    }
}
