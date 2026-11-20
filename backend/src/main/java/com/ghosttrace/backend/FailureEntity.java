
package com.ghosttrace.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "failures")
public class FailureEntity {
    @Id
    @Column(nullable = false, length = 80)
    private String failureId;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 1000)
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FailureSeverity severity;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(length = 32)
    private String traceId;

    protected FailureEntity() { }

    public FailureEntity(
            String failureId,
            String serviceName,
            String errorMessage,
            FailureSeverity severity
    ) {
        this(failureId, serviceName, errorMessage, severity, null);
    }

    public FailureEntity(
            String failureId,
            String serviceName,
            String errorMessage,
            FailureSeverity severity,
            String traceId
    ) {
        this.failureId = failureId;
        this.serviceName = serviceName;
        this.errorMessage = errorMessage;
        this.severity = severity;
        this.traceId = traceId;
        this.occurredAt = Instant.now();
    }

    public String getFailureId() { return failureId; }
    public String getServiceName() { return serviceName; }
    public String getErrorMessage() { return errorMessage; }
    public FailureSeverity getSeverity() { return severity; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getTraceId() { return traceId; }
}
