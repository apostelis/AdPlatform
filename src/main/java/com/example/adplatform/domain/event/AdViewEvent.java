package com.example.adplatform.domain.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event that represents an advertisement view.
 * This event is triggered when a user views an advertisement.
 */
@Data
@Builder
public class AdViewEvent implements AdEvent {
    
    private final Long advertisementId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String sessionId;
    private final String deviceType;
    private final String browserInfo;
    private final String ipAddress;
    private final Map<String, Object> contextData;
    private final Integer viewDurationSeconds;
    private final Boolean isCompleteView;
    
    @Override
    public AdEventType getEventType() {
        return isCompleteView ? AdEventType.COMPLETE_VIEW : AdEventType.VIEW;
    }
}