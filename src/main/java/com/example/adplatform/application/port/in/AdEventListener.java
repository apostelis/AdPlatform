package com.example.adplatform.application.port.in;

import com.example.adplatform.domain.event.AdClickEvent;
import com.example.adplatform.domain.event.AdEvent;
import com.example.adplatform.domain.event.AdViewEvent;

/**
 * Port for listening to advertisement events.
 * This is part of the event-driven architecture for tracking advertisement views and interactions.
 * It follows the hexagonal architecture pattern as an incoming port.
 */
public interface AdEventListener {
    
    /**
     * Handles an advertisement event.
     * This method is called when any advertisement event is published.
     *
     * @param event the event to handle
     */
    void onEvent(AdEvent event);
    
    /**
     * Handles an advertisement view event.
     * This method is called when an advertisement view event is published.
     *
     * @param viewEvent the view event to handle
     */
    default void onViewEvent(AdViewEvent viewEvent) {
        onEvent(viewEvent);
    }
    
    /**
     * Handles an advertisement click event.
     * This method is called when an advertisement click event is published.
     *
     * @param clickEvent the click event to handle
     */
    default void onClickEvent(AdClickEvent clickEvent) {
        onEvent(clickEvent);
    }
}