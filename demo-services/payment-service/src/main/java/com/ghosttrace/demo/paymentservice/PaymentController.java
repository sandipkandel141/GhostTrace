package com.ghosttrace.demo.paymentservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final RestClient inventory;
    public PaymentController(RestClient.Builder builder, @Value("${inventory.service.url}") String url) {
        this.inventory = builder.baseUrl(url).build();
    }
    public record PaymentRequest(String orderId, String sku, int quantity) {}
    public record InventoryResponse(String sku, int requestedQuantity, boolean available) {}
    public record PaymentResponse(String orderId, String status, String detail) {}

    @PostMapping
    public PaymentResponse process(@RequestBody PaymentRequest request) {
        if (request.orderId() == null || request.orderId().isBlank() || request.sku() == null || request.sku().isBlank() || request.quantity() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid orderId, sku and positive quantity required");
        try {
            InventoryResponse stock = inventory.get().uri(uri -> uri.path("/api/inventory/{sku}").queryParam("quantity", request.quantity()).build(request.sku()))
                .retrieve().body(InventoryResponse.class);
            if (stock == null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty inventory response");
            if (!stock.available()) return new PaymentResponse(request.orderId(), "REJECTED", "Out of stock");
            // Demo simulation: no payment details are collected and no money moves.
            return new PaymentResponse(request.orderId(), "APPROVED", "Simulated payment approved");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Inventory service unavailable", ex);
        }
    }
}
