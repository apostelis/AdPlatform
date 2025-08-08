package com.example.adplatform.application.service.targeting;

import com.example.adplatform.domain.model.Advertisement;

import java.util.List;
import java.util.Map;

/**
 * Strategy contract for advertisement targeting.
 * Each strategy encapsulates one type of targeting and can filter
 * a list of advertisements or match a single advertisement against
 * provided criteria.
 */
public interface TargetingStrategy {

    /**
     * @return unique strategy key (e.g., "geo", "bio", "mood")
     */
    String key();

    /**
     * Filters a list of advertisements using the provided criteria map.
     * The required keys are strategy-specific.
     */
    List<Advertisement> filter(List<Advertisement> advertisements, Map<String, Object> criteria);

    /**
     * Checks whether an advertisement matches provided criteria.
     * The required keys are strategy-specific.
     */
    boolean matches(Advertisement advertisement, Map<String, Object> criteria);
}
