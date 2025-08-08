package com.example.adplatform.infrastructure.messaging;

import com.example.adplatform.application.port.out.AdvertisementEventPublisher;
import com.example.adplatform.domain.event.AdvertisementInteractedEvent;
import com.example.adplatform.domain.event.AdvertisementViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter that publishes events using Spring's ApplicationEventPublisher.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAdvertisementEventPublisher implements AdvertisementEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(AdvertisementViewedEvent event) {
        log.debug("Publishing AdvertisementViewedEvent: {}", event);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(AdvertisementInteractedEvent event) {
        log.debug("Publishing AdvertisementInteractedEvent: {}", event);
        applicationEventPublisher.publishEvent(event);
    }
}
