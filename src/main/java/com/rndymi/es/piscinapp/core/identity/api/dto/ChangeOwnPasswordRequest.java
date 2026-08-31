package com.rndymi.es.piscinapp.core.identity.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeOwnPasswordRequest(

        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(
                min = 12,
                max = 128
        )
        String newPassword
) {
}
