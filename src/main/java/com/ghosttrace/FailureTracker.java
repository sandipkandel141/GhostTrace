
package com.ghosttrace;

import java.util.ArrayList;
import java.util.List;

public class FailureTracker {
    private final List<FailureRecord> failures = new ArrayList<>();

    public void addFailure(FailureRecord failure) {
        failures.add(failure);
        System.out.println("Failure recorded: " + failure.getFailureId());
    }

    public void displayAllFailures() {
        if (failures.isEmpty()) {
            System.out.println("No failures recorded.");
            return;
        }

        for (FailureRecord failure : failures) {
            failure.displayFailure();
        }
    }

    public FailureRecord findFailureById(String failureId) {
        for (FailureRecord failure : failures) {
            if (failure.getFailureId().equalsIgnoreCase(failureId)) {
                return failure;
            }
        }
        return null;
    }

    public void displayFailuresByService(String serviceName) {
        boolean found = false;

        for (FailureRecord failure : failures) {
            if (failure.getServiceName().equalsIgnoreCase(serviceName)) {
                failure.displayFailure();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No failures found for: " + serviceName);
        }
    }

    public int getFailureCount() {
        return failures.size();
    }
}
