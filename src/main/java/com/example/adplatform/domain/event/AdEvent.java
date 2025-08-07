package com.example.adplatform.domain.event;

import java.time.LocalDateTime;

/**
 * Base interface for all advertisement-related events.
 * This is part of the event-driven architecture for tracking advertisement views and interactions.
 */
public interface AdEvent {
    
    /**
     * Gets the unique identifier of the advertisement associated with this event.
     *
     * @return the advertisement ID
     */
    Long getAdvertisementId();
    
    /**
     * Gets the timestamp when the event occurred.
     *
     * @return the event timestamp
     */
    LocalDateTime getTimestamp();
    
    /**
     * Gets the type of the event.
     *
     * @return the event type
     */
    AdEventType getEventType();
    
    /**
     * Enumeration of advertisement event types.
     */
    enum AdEventType {
        VIEW,           // Advertisement was viewed
        CLICK,          // Advertisement was clicked
        CONVERSION,     // User completed a conversion action after viewing the ad
        SKIP,           // User skipped the advertisement
        COMPLETE_VIEW,  // User watched the entire advertisement
        SHARE,          // User shared the advertisement
        LIKE,           // User liked the advertisement
        DISLIKE         // User disliked the advertisement
    }
}