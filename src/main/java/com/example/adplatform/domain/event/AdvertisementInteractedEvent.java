package com.example.adplatform.domain.event;

import java.time.Instant;

/**
 * Domain event emitted when an advertisement interaction occurs (e.g., CLICK, LIKE, SHARE).
 */
public record AdvertisementInteractedEvent(Long advertisementId, String interactionType, Instant occurredAt) {
}