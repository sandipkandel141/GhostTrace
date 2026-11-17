# GhostTrace Backend — Milestone 2

Java 17 / Spring Boot 3.5.6 REST API with Bean Validation, Spring Data JPA, and an in-memory H2 database.

## Requirements
- JDK 17
- Maven 3.6.3+ (or compatible Maven 3.x)

## Run
```bash
cd backend
mvn test
mvn spring-boot:run
```

## Endpoints
- `POST /api/failures` — create a failure (201; 400 for invalid input; 409 for duplicate ID)
- `GET /api/failures` — list all failures
- `GET /api/failures?serviceName=PaymentService` — filter by service (case-insensitive)
- `GET /api/failures/ERR-101` — find by ID (404 if absent)
- `GET /api/failures/count` — count records

## Example request
```bash
curl -i -X POST http://localhost:8080/api/failures \
  -H 'Content-Type: application/json' \
  -d '{"failureId":"ERR-101","serviceName":"PaymentService","errorMessage":"Database timeout","severity":"HIGH"}'

curl http://localhost:8080/api/failures
curl http://localhost:8080/api/failures/count
```

Allowed severity values: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.

## Limitations
- H2 runs in memory: all records disappear on application restart.
- This is a local learning build, not production-hardened (no authentication, pagination, rate limits, or concurrency-safe duplicate-ID handling).
- PostgreSQL, distributed tracing, AI, Docker, and dashboard are later milestones.
