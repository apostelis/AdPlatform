package com.example.adplatform.domain.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event that represents an advertisement click.
 * This event is triggered when a user clicks on an advertisement.
 */
@Data
@Builder
public class AdClickEvent implements AdEvent {
    
    private final Long advertisementId;
    private final LocalDateTime timestamp;
    private final String userId;
    private final String sessionId;
    private final String deviceType;
    private final String browserInfo;
    private final String ipAddress;
    private final Map<String, Object> contextData;
    private final String targetUrl;
    private final Integer xPosition;
    private final Integer yPosition;
    private final Boolean resultedInConversion;
    private final String referrer;
    
    @Override
    public AdEventType getEventType() {
        return resultedInConversion ? AdEventType.CONVERSION : AdEventType.CLICK;
    }
}