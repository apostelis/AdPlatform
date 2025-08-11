package com.example.adplatform.application.port.in;

import com.example.adplatform.domain.model.Advertisement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Viewing policy decides ordering (priority) of advertisements for display.
 * Default policy:
 * - Any ad with an active override window (now within [overrideStart, overrideEnd]) is topped.
 * - Among active overrides, order by weight descending, then createdAt desc as tie-breaker.
 * - Remaining ads ordered by weight descending, then createdAt desc.
 *
 * Fair-view policy extension:
 * - When selecting an ad to show, pick proportionally to weight among eligible ads.
 * - If any override is active, selection happens within the override group.
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

    /**
     * Selects a single advertisement for display using the fair-view policy (weighted random by weight).
     * If any override is active at the given time, the selection is performed only among override-active ads.
     * If no eligible ads or all weights are non-positive, returns null.
     */
    Advertisement selectOneFair(List<Advertisement> advertisements, LocalDateTime now, Random random);

    /**
     * Overload using system time and default Random.
     */
    default Advertisement selectOneFair(List<Advertisement> advertisements) {
        return selectOneFair(advertisements, LocalDateTime.now(), new Random());
    }

    /**
     * Returns a list where the first element is selected using the fair-view policy, and the rest follow the
     * deterministic orderForDisplay sequence. Useful for endpoints that return lists but wish to prefer a fair-first item.
     */
    List<Advertisement> orderForDisplayWithFairFirst(List<Advertisement> advertisements, LocalDateTime now, Random random);

    /**
     * Overload using system time and default Random.
     */
    default List<Advertisement> orderForDisplayWithFairFirst(List<Advertisement> advertisements) {
        return orderForDisplayWithFairFirst(advertisements, LocalDateTime.now(), new Random());
    }
}
