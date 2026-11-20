
package com.ghosttrace.demo.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Test
    void reportsInventoryFailureToGhostTrace() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient inventoryClient = mock(RestClient.class);
        RestClient ghosttraceClient = mock(RestClient.class);

        RestClient.RequestHeadersUriSpec inventoryRequest =
                mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec inventoryResponse =
                mock(RestClient.ResponseSpec.class);

        RestClient.RequestBodyUriSpec failureRequest =
                mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec failureBody =
                mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec failureResponse =
                mock(RestClient.ResponseSpec.class);

        RestClient.Builder inventoryBuilder = mock(RestClient.Builder.class);
        RestClient.Builder backendBuilder = mock(RestClient.Builder.class);

        when(builder.clone()).thenReturn(inventoryBuilder, backendBuilder);
        when(inventoryBuilder.baseUrl(anyString())).thenReturn(inventoryBuilder);
        when(backendBuilder.baseUrl(anyString())).thenReturn(backendBuilder);
        when(inventoryBuilder.build()).thenReturn(inventoryClient);
        when(backendBuilder.build()).thenReturn(ghosttraceClient);

        when(inventoryClient.get()).thenReturn(inventoryRequest);
        when(inventoryRequest.uri(any(java.util.function.Function.class)))
                .thenReturn(inventoryRequest);
        when(inventoryRequest.retrieve()).thenReturn(inventoryResponse);
        when(inventoryResponse.body(
                PaymentController.InventoryResponse.class))
                .thenThrow(new RestClientException("Connection refused"));

        when(ghosttraceClient.post()).thenReturn(failureRequest);
        when(failureRequest.uri("/api/failures")).thenReturn(failureBody);
        when(failureBody.body(any(Object.class))).thenReturn(failureBody);
        when(failureBody.retrieve()).thenReturn(failureResponse);

        PaymentController controller = new PaymentController(
                builder,
                "http://localhost:8083",
                "http://localhost:8080"
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.process(
                        new PaymentController.PaymentRequest(
                                "TEST-001", "BOOK-101", 2
                        )
                )
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());

        verify(ghosttraceClient).post();
        verify(failureBody).body(argThat((Object event) -> {
            Map<?, ?> failure = (Map<?, ?>) event;
            return "payment-service".equals(failure.get("serviceName"))
                    && "Inventory service unavailable".equals(
                            failure.get("errorMessage"))
                    && "HIGH".equals(failure.get("severity"));
        }));
    }
}

