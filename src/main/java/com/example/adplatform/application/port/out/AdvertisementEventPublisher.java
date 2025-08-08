package com.example.adplatform.application.port.out;

import com.example.adplatform.domain.event.AdvertisementInteractedEvent;
import com.example.adplatform.domain.event.AdvertisementViewedEvent;

/**
 * Output port for publishing advertisement-related domain events.
 */
public interface AdvertisementEventPublisher {
    void publish(AdvertisementViewedEvent event);
    void publish(AdvertisementInteractedEvent event);
}