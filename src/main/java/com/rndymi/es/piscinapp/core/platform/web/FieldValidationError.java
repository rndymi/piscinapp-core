package com.rndymi.es.piscinapp.core.platform.web;

public record FieldValidationError(
        String field,
        String message
) {
}
