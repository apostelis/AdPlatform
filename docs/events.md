# Event-driven architecture for advertisement tracking

This project implements a lightweight event-driven architecture for tracking advertisement views and interactions, aligned with DDD and Hexagonal principles.

## Domain events

- AdvertisementViewedEvent(advertisementId, occurredAt)
- AdvertisementInteractedEvent(advertisementId, interactionType, occurredAt)

These immutable domain events are defined in the domain layer under `domain/event`.

## Ports & Adapters

- Output Port: `AdvertisementEventPublisher` (application.port.out) abstracts the event publishing mechanism.
- Adapter: `SpringAdvertisementEventPublisher` publishes events via Spring's `ApplicationEventPublisher`.

You can add alternative adapters later (e.g., Kafka, RabbitMQ) without changing the core domain/application logic.

## Use cases / Service

The `AdvertisementService` exposes methods:
- `trackAdvertisementView(Long id)`
- `trackAdvertisementInteraction(Long id, String interactionType)`

The implementation ensures the advertisement exists and then publishes the corresponding event.

## API endpoints

- POST `/api/v1/advertisements/{id}/view` — Track a view event.
- POST `/api/v1/advertisements/{id}/interactions?type=CLICK` — Track an interaction event.

Both return `202 Accepted` indicating the event was accepted for processing.

## Next steps

- Introduce a message broker adapter (Kafka/RabbitMQ) for distributed processing.
- Add persistence/metrics consumer(s) for analytics (e.g., increment counters, store timelines).
- Define a formal event schema/versioning strategy.
