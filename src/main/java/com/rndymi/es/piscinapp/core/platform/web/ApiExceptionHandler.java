package com.rndymi.es.piscinapp.core.platform.web;

import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeAccountConflictException;
import com.rndymi.es.piscinapp.core.employees.application.exception.EmployeeNotFoundException;
import com.rndymi.es.piscinapp.core.identity.application.exception.InvalidCurrentPasswordException;
import com.rndymi.es.piscinapp.core.identity.application.exception.LastAdminConflictException;
import com.rndymi.es.piscinapp.core.identity.application.exception.UserAccountNotFoundException;
import com.rndymi.es.piscinapp.core.identity.application.exception.UsernameConflictException;
import com.rndymi.es.piscinapp.core.maintenance.application.exception.InactiveResourceException;
import com.rndymi.es.piscinapp.core.maintenance.application.exception.MaintenanceActivityNotFoundException;
import com.rndymi.es.piscinapp.core.maintenance.application.exception.PoolMaintenanceActivityConflictException;
import com.rndymi.es.piscinapp.core.pools.application.exception.PoolNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(
            UserAccountNotFoundException.class
    )
    ProblemDetail handleUserNotFound(
            UserAccountNotFoundException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                ApiErrorCode
                        .IDENTITY_USER_NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(
            UsernameConflictException.class
    )
    ProblemDetail handleUsernameConflict(
            UsernameConflictException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                ApiErrorCode
                        .IDENTITY_USERNAME_CONFLICT,
                request
        );
    }

    @ExceptionHandler(
            InvalidCurrentPasswordException.class
    )
    ProblemDetail handleInvalidCurrentPassword(
            InvalidCurrentPasswordException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                ApiErrorCode
                        .IDENTITY_INVALID_CURRENT_PASSWORD,
                request
        );
    }

    @ExceptionHandler(
            LastAdminConflictException.class
    )
    ProblemDetail handleLastAdminConflict(
            LastAdminConflictException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                ApiErrorCode
                        .IDENTITY_LAST_ADMIN_CONFLICT,
                request
        );
    }

    @ExceptionHandler(
            EmployeeNotFoundException.class
    )
    ProblemDetail handleEmployeeNotFound(
            EmployeeNotFoundException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                ApiErrorCode
                        .EMPLOYEE_NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(
            EmployeeAccountConflictException.class
    )
    ProblemDetail handleEmployeeAccountConflict(
            EmployeeAccountConflictException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                ApiErrorCode
                        .EMPLOYEE_ACCOUNT_CONFLICT,
                request
        );
    }

    @ExceptionHandler(
            PoolNotFoundException.class
    )
    ProblemDetail handlePoolNotFound(
            PoolNotFoundException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                ApiErrorCode
                        .POOL_NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(
            MaintenanceActivityNotFoundException.class
    )
    ProblemDetail handleMaintenanceActivityNotFound(
            MaintenanceActivityNotFoundException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                ApiErrorCode
                        .MAINTENANCE_ACTIVITY_NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(
            PoolMaintenanceActivityConflictException.class
    )
    ProblemDetail handlePoolMaintenanceActivityConflict(
            PoolMaintenanceActivityConflictException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                ApiErrorCode
                        .POOL_MAINTENANCE_ACTIVITY_CONFLICT,
                request
        );
    }

    @ExceptionHandler(
            InactiveResourceException.class
    )
    ProblemDetail handleInactiveResource(
            InactiveResourceException exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                ApiErrorCode
                        .RESOURCE_INACTIVE,
                request
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    ProblemDetail handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        ProblemDetail detail =
                problem(
                        HttpStatus.BAD_REQUEST,
                        "Request validation failed",
                        ApiErrorCode
                                .VALIDATION_ERROR,
                        request
                );

        List<FieldValidationError> errors =
                exception
                        .getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(
                                error ->
                                        new FieldValidationError(
                                                error.getField(),
                                                error.getDefaultMessage()
                                        )
                        )
                        .sorted(
                                Comparator
                                        .comparing(
                                                FieldValidationError::field
                                        )
                                        .thenComparing(
                                                FieldValidationError::message
                                        )
                        )
                        .toList();

        detail.setProperty(
                "errors",
                errors
        );

        return detail;
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    ProblemDetail handleBadRequest(
            Exception exception,
            HttpServletRequest request
    ) {

        return problem(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                ApiErrorCode
                        .VALIDATION_ERROR,
                request
        );
    }

    private ProblemDetail problem(
            HttpStatus status,
            String message,
            ApiErrorCode code,
            HttpServletRequest request
    ) {

        ProblemDetail detail =
                ProblemDetail
                        .forStatusAndDetail(
                                status,
                                message
                        );

        detail.setTitle(
                status.getReasonPhrase()
        );

        detail.setInstance(
                URI.create(
                        request.getRequestURI()
                )
        );

        detail.setProperty(
                "code",
                code.name()
        );

        return detail;
    }
}
