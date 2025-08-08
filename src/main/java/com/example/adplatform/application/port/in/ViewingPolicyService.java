package com.example.adplatform.application.port.in;

import com.example.adplatform.domain.model.Advertisement;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Viewing policy decides ordering (priority) of advertisements for display.
 * Default policy:
 * - Any ad with an active override window (now within [overrideStart, overrideEnd]) is topped.
 * - Among active overrides, order by weight descending, then createdAt desc as tie-breaker.
 * - Remaining ads ordered by weight descending, then createdAt desc.
 */
public interface ViewingPolicyService {

    /**
     * Orders the provided advertisements according to the viewing policy relative to the provided time.
     * @param advertisements input ads (assumed already filtered by targeting)
     * @param now time reference
     * @return ordered list for display
     */
    List<Advertisement> orderForDisplay(List<Advertisement> advertisements, LocalDateTime now);

    /**
     * Convenience method that uses system clock.
     */
    default List<Advertisement> orderForDisplay(List<Advertisement> advertisements) {
        return orderForDisplay(advertisements, LocalDateTime.now());
    }
}
