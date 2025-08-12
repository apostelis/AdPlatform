package com.example.adplatform.application.service.targeting;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.GeoTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GeoTargetingStrategy implements TargetingStrategy {

    public static final String KEY = "geo";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<Advertisement> filter(List<Advertisement> advertisements, Map<String, Object> criteria) {
        if (advertisements == null || advertisements.isEmpty()) {
            log.debug("Geo targeting: no advertisements to filter");
            return Collections.emptyList();
        }
        List<Advertisement> result = advertisements.stream()
                .filter(ad -> matches(ad, criteria))
                .collect(Collectors.toList());
        log.debug("Geo targeting: filtered {} out of {} advertisements", result.size(), advertisements.size());
        return result;
    }

    @Override
    public boolean matches(Advertisement advertisement, Map<String, Object> criteria) {
        String countryCode = (String) criteria.get("countryCode");
        String region = (String) criteria.get("region");
        String city = (String) criteria.get("city");
        Double latitude = (Double) criteria.get("latitude");
        Double longitude = (Double) criteria.get("longitude");
        if (advertisement.getGeoTargets() == null || advertisement.getGeoTargets().isEmpty()) {
            log.trace("Geo targeting: advertisement {} has no geo targets", advertisement.getId());
            return false;
        }
        boolean hasIncludeMatch = advertisement.getGeoTargets().stream()
                .filter(GeoTarget::isInclude)
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));
        boolean hasExcludeMatch = advertisement.getGeoTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));
        boolean match = hasIncludeMatch && !hasExcludeMatch;
        log.trace("Geo targeting: advertisement {} match={}, includeMatch={}, excludeMatch={}", advertisement.getId(), match, hasIncludeMatch, hasExcludeMatch);
        return match;
    }
}
