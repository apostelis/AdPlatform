package com.example.adplatform.application.port.out;

import com.example.adplatform.domain.event.AdEvent;
import com.example.adplatform.domain.event.AdClickEvent;
import com.example.adplatform.domain.event.AdViewEvent;

/**
 * Port for publishing advertisement events.
 * This is part of the event-driven architecture for tracking advertisement views and interactions.
 * It follows the hexagonal architecture pattern as an outgoing port.
 */
public interface AdEventPublisher {
    
    /**
     * Publishes an advertisement event.
     *
     * @param event the event to publish
     */
    void publishEvent(AdEvent event);
    
    /**
     * Publishes an advertisement view event.
     * This is a convenience method for publishing view events.
     *
     * @param viewEvent the view event to publish
     */
    default void publishViewEvent(AdViewEvent viewEvent) {
        publishEvent(viewEvent);
    }
    
    /**
     * Publishes an advertisement click event.
     * This is a convenience method for publishing click events.
     *
     * @param clickEvent the click event to publish
     */
    default void publishClickEvent(AdClickEvent clickEvent) {
        publishEvent(clickEvent);
    }
}