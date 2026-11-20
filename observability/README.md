# GhostTrace Distributed Tracing

GhostTrace uses OpenTelemetry and Jaeger to trace requests across three Spring Boot microservices.

## Requirements

- Java 17
- Maven
- Docker Desktop

## Start Jaeger

```bash
docker run -d \
  --name ghosttrace-jaeger \
  -p 16686:16686 \
  -p 4317:4317 \
  -p 4318:4318 \
  jaegertracing/all-in-one:1.76.0
```

If the container already exists:

```bash
docker start ghosttrace-jaeger
```

## Download OpenTelemetry

From the repository root:

```bash
./observability/download-agent.sh
```

## Start Microservices

Run each command in a separate terminal:

```bash
./observability/start-service.sh inventory-service
```

```bash
./observability/start-service.sh payment-service
```

```bash
./observability/start-service.sh order-service
```

## Generate a Distributed Trace

```bash
curl -i -X POST "http://127.0.0.1:8081/api/orders" \
  -H "Content-Type: application/json" \
  -d '{"sku":"BOOK-101","quantity":2}'
```

## View Traces

Open http://localhost:16686/search

Select order-service and click Find Traces.

A successful distributed trace includes:

- order-service
- payment-service
- inventory-service

## OpenTelemetry Configuration

Traces are exported using OTLP HTTP/protobuf to:

http://localhost:4318

The Java agent is downloaded separately and excluded from Git.
## Automatic Failure Reporting (Phase 5C)

GhostTrace connects distributed tracing with automatic
failure reporting.

When Payment Service cannot communicate with Inventory
Service:

1. Payment Service detects the connection failure.
2. Payment Service captures the current OpenTelemetry trace ID.
3. It sends a failure report to GhostTrace Backend.
4. The backend saves the failure and trace ID in PostgreSQL.
5. Developers can search for the trace ID in Jaeger to
   investigate the failure.

### Failure Reporting Endpoint

POST http://localhost:8080/api/failures

Example request:

{
  "failureId": "PAY-123",
  "serviceName": "payment-service",
  "errorMessage": "Inventory service unavailable",
  "severity": "HIGH",
  "traceId": "9974588509a755df5040856007ea5c62"
}

### Automated Testing

- Backend: 5 tests
- Payment Service: 2 tests
- Total: 7 tests

### Verified Integration

A simulated Inventory Service outage returned HTTP 502.
Payment Service automatically reported the failure to
GhostTrace Backend, and the matching trace was found
in Jaeger.
