package com.ghosttrace.demo.orderservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final RestClient payment;
    public OrderController(RestClient.Builder builder, @Value("${payment.service.url}") String url) {
        this.payment = builder.baseUrl(url).build();
    }
    public record OrderRequest(String sku, int quantity) {}
    public record PaymentRequest(String orderId, String sku, int quantity) {}
    public record PaymentResponse(String orderId, String status, String detail) {}
    public record OrderResponse(String orderId, String status, String detail) {}

    @PostMapping
    public OrderResponse create(@RequestBody OrderRequest request) {
        if (request.sku() == null || request.sku().isBlank() || request.quantity() < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid sku and positive quantity required");
        String orderId = UUID.randomUUID().toString();
        try {
            PaymentResponse result = payment.post().uri("/api/payments")
                .body(new PaymentRequest(orderId, request.sku(), request.quantity()))
                .retrieve().body(PaymentResponse.class);
            if (result == null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty payment response");
            return new OrderResponse(orderId, result.status().equals("APPROVED") ? "CONFIRMED" : "REJECTED", result.detail());
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Payment service unavailable", ex);
        }
    }
}
