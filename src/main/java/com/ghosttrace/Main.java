
package com.ghosttrace;

public class Main {
    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("       GHOSTTRACE v1.0");
        System.out.println("  Software Failure Tracker");
        System.out.println("================================");

        FailureTracker tracker = new FailureTracker();

        tracker.addFailure(new FailureRecord(
                "ERR-101",
                "PaymentService",
                "Database connection timeout",
                "HIGH"
        ));

        tracker.addFailure(new FailureRecord(
                "ERR-102",
                "LoginService",
                "Invalid authentication token",
                "MEDIUM"
        ));

        tracker.addFailure(new FailureRecord(
                "ERR-103",
                "PaymentService",
                "Payment gateway unavailable",
                "CRITICAL"
        ));

        System.out.println("\n=== ALL FAILURES ===");
        tracker.displayAllFailures();

        System.out.println("\n=== SEARCH BY ID ===");
        FailureRecord result = tracker.findFailureById("ERR-102");

        if (result != null) {
            result.displayFailure();
        } else {
            System.out.println("Failure not found.");
        }

        System.out.println("\n=== PAYMENT SERVICE FAILURES ===");
        tracker.displayFailuresByService("PaymentService");

        System.out.println("\nTotal failures: " + tracker.getFailureCount());
    }
}
