# GhostTrace Phase 5A — Demo microservices

Java 17 / Spring Boot 3.5.6. These are simulated services only; no actual charges, inventory reservation, persistence, or distributed tracing yet.

## Start (three separate terminals)

Ensure Java 17 is selected in each terminal:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

From the GhostTrace repository root, run one command in each terminal:

```bash
cd demo-services/inventory-service && mvn spring-boot:run
cd demo-services/payment-service && mvn spring-boot:run
cd demo-services/order-service && mvn spring-boot:run
```

The services listen on 8083, 8082, and 8081, respectively. The existing GhostTrace backend on 8080 is separate and does not need to run for Phase 5A.

## Exercise the chain

```bash
curl -i -X POST http://127.0.0.1:8081/api/orders -H 'Content-Type: application/json' -d '{"sku":"BOOK-101","quantity":2}'
```

Expected status: HTTP 200 with JSON `status: CONFIRMED`.

Out-of-stock scenario:

```bash
curl -i -X POST http://127.0.0.1:8081/api/orders -H 'Content-Type: application/json' -d '{"sku":"TEST-OUT","quantity":1}'
```

Expected JSON `status: REJECTED`.

## Tests

```bash
for service in inventory-service payment-service order-service; do (cd "demo-services/$service" && mvn test) || exit 1; done
```

These are basic Spring context smoke tests. End-to-end HTTP behavior should also be verified using the commands above. Phase 5B will introduce tracing and Phase 5C will report failures to GhostTrace.
