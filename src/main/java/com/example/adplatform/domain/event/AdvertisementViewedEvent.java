package com.example.adplatform.domain.event;

import java.time.Instant;

/**
 * Domain event emitted when an advertisement is viewed.
 */
public record AdvertisementViewedEvent(Long advertisementId, Instant occurredAt) {
}