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
