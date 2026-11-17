package com.ghosttrace.backend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFailureRequest(
    @NotBlank @Size(max = 80) String failureId,
    @NotBlank @Size(max = 100) String serviceName,
    @NotBlank @Size(max = 1000) String errorMessage,
    @NotNull FailureSeverity severity
) { }
