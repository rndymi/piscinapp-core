package com.rndymi.es.piscinapp.core.execution.application;

import java.util.UUID;

public record OperationalActor(
        UUID accountId,
        UUID employeeId
) {
}
