# Order Events Platform (Spring Boot + Kafka + PostgreSQL)

Event-driven order processing with two microservices. Reliable messaging is the point of the project:
transactional outbox on the producer, idempotent consumer, retries and a dead-letter topic.

## Architecture

```
POST /api/orders
      |
 order-service ---(1 DB transaction)---> orders + outbox_events  (PostgreSQL: orders_db)
      |
 OutboxPublisher (polls every 1s)
      |
   Kafka topic: order.created (3 partitions, key = orderId)
      |
 notification-service (consumer group)
      |-- idempotency check (processed_events)
      |-- saves notification (PostgreSQL: notifications_db)
      '-- on repeated failure -> order.created.DLT
```

## Tech
Java 17, Spring Boot 3.3, Spring Data JPA, Spring Kafka, PostgreSQL 16, Flyway, Docker Compose, JUnit 5 + Mockito.

## Run
```bash
docker compose up --build
```

## Try it
```bash
# create an order
curl -X POST localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail":"test@example.com","amount":499.00}'

# watch notification-service logs: "Notification sent to test@example.com ..."
docker compose logs -f notification-service

# poison message -> retries skipped (bad payload) -> lands in order.created.DLT
docker compose exec kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server kafka:19092 --topic order.created
> this is not json

docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server kafka:19092 --topic order.created.DLT --from-beginning
```

## Run tests
```bash
cd order-service && mvn test
```

## Key design decisions
| Problem | Solution |
|---|---|
| Order saved but event lost (dual-write) | Transactional outbox table, relayed to Kafka |
| Duplicate delivery (at-least-once) | `processed_events` table, checked in the same transaction |
| Poison messages blocking a partition | Retry with backoff, then dead-letter topic |
| Per-order ordering | Kafka message key = orderId |
| Schema drift | Flyway migrations, `ddl-auto: validate` |

## Next steps (good for extending the project)
- JWT auth + role-based access on order-service
- Testcontainers integration test (Postgres + Kafka)
- GitHub Actions CI (build, test, push Docker images)
- Deploy to AWS: images in ECR, run on ECS Fargate, RDS PostgreSQL, Amazon MSK for Kafka, logs in CloudWatch
- Replace the log line with real email via AWS SES

## Resume bullets (edit numbers to match what you measure)
**Order Events Platform** | Java, Spring Boot, Kafka, PostgreSQL, Docker
- Built an event-driven order system with two Spring Boot microservices communicating over Apache Kafka
- Implemented the transactional outbox pattern to guarantee no lost events between PostgreSQL writes and Kafka publishes
- Designed idempotent consumers with retry, backoff and a dead-letter topic to handle duplicate and malformed messages
- Containerised the stack with Docker Compose; managed schema with Flyway; wrote unit tests with JUnit and Mockito
