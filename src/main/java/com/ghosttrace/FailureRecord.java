
package com.ghosttrace;

public class FailureRecord {
    private final String failureId;
    private final String serviceName;
    private final String errorMessage;
    private final String severity;

    public FailureRecord(String failureId, String serviceName,
                         String errorMessage, String severity) {
        this.failureId = failureId;
        this.serviceName = serviceName;
        this.errorMessage = errorMessage;
        this.severity = severity;
    }

    public String getFailureId() {
        return failureId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSeverity() {
        return severity;
    }

    public void displayFailure() {
        System.out.println("-----------------------------");
        System.out.println("Failure ID: " + failureId);
        System.out.println("Service: " + serviceName);
        System.out.println("Error: " + errorMessage);
        System.out.println("Severity: " + severity);
    }
}
