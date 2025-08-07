package com.example.adplatform.infrastructure.event;

import com.example.adplatform.application.port.out.AdEventPublisher;
import com.example.adplatform.domain.event.AdEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementation of the AdEventPublisher interface that uses Spring's ApplicationEventPublisher.
 * This adapter converts domain events to Spring application events and publishes them.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAdEventPublisher implements AdEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    @Override
    public void publishEvent(AdEvent event) {
        log.debug("Publishing advertisement event: type={}, adId={}", 
                event.getEventType(), event.getAdvertisementId());
        
        // Convert domain event to Spring application event and publish it
        applicationEventPublisher.publishEvent(new SpringAdEventWrapper(event));
    }
    
    /**
     * Wrapper class that adapts our domain events to Spring application events.
     */
    private static class SpringAdEventWrapper {
        private final AdEvent adEvent;
        
        public SpringAdEventWrapper(AdEvent adEvent) {
            this.adEvent = adEvent;
        }
        
        public AdEvent getAdEvent() {
            return adEvent;
        }
    }
}