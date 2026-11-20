package com.ghosttrace.demo.paymentservice;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final RestClient inventory;
    private final RestClient ghosttrace;

    public PaymentController(
            RestClient.Builder builder,
            @Value("${inventory.service.url}") String inventoryUrl,
            @Value("${ghosttrace.backend.url}") String backendUrl
    ) {
        this.inventory = builder.clone().baseUrl(inventoryUrl).build();
        this.ghosttrace = builder.clone().baseUrl(backendUrl).build();
    }

    public record PaymentRequest(String orderId, String sku, int quantity) {}
    public record InventoryResponse(String sku, int requestedQuantity, boolean available) {}
    public record PaymentResponse(String orderId, String status, String detail) {}

    @PostMapping
    public PaymentResponse process(@RequestBody PaymentRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()
                || request.sku() == null || request.sku().isBlank()
                || request.quantity() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valid orderId, sku and positive quantity required"
            );
        }

        try {
            InventoryResponse stock = inventory.get()
                    .uri(uri -> uri.path("/api/inventory/{sku}")
                            .queryParam("quantity", request.quantity())
                            .build(request.sku()))
                    .retrieve()
                    .body(InventoryResponse.class);

            if (stock == null) {
                reportFailure("Empty inventory response");
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY, "Empty inventory response"
                );
            }

            if (!stock.available()) {
                return new PaymentResponse(
                        request.orderId(), "REJECTED", "Out of stock"
                );
            }

            return new PaymentResponse(
                    request.orderId(), "APPROVED", "Simulated payment approved"
            );

        } catch (RestClientException ex) {
            reportFailure("Inventory service unavailable");
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Inventory service unavailable",
                    ex
            );
        }
    }

    private void reportFailure(String message) {
        SpanContext context = Span.current().getSpanContext();
        String traceId = context.isValid() ? context.getTraceId() : null;

        Map<String, Object> event = new java.util.HashMap<>();
        event.put("failureId", "PAY-" + UUID.randomUUID());
        event.put("serviceName", "payment-service");
        event.put("errorMessage", message);
        event.put("severity", "HIGH");
        event.put("traceId", traceId);

        try {
            ghosttrace.post()
                    .uri("/api/failures")
                    .body(event)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            // Failure reporting must not hide the original payment error.
            System.err.println(
                    "Could not report failure to GhostTrace: " + ex.getMessage()
            );
        }
    }
}