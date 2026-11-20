package com.ghosttrace.backend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateFailureRequest(
        @NotBlank @Size(max = 80) String failureId,
        @NotBlank @Size(max = 100) String serviceName,
        @NotBlank @Size(max = 1000) String errorMessage,
        @NotNull FailureSeverity severity,
        @Pattern(regexp = "[0-9a-fA-F]{32}",
                message = "traceId must contain 32 hexadecimal characters")
        String traceId
) {
    // Keeps existing four-argument Java calls working.
    public CreateFailureRequest(
            String failureId,
            String serviceName,
            String errorMessage,
            FailureSeverity severity
    ) {
        this(failureId, serviceName, errorMessage, severity, null);
    }
}
