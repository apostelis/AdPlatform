package com.example.adplatform.application.service.targeting;

import com.example.adplatform.domain.model.Advertisement;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GeoTargetingStrategy implements TargetingStrategy {

    public static final String KEY = "geo";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<Advertisement> filter(List<Advertisement> advertisements, Map<String, Object> criteria) {
        if (advertisements == null || advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        String countryCode = (String) criteria.get("countryCode");
        String region = (String) criteria.get("region");
        String city = (String) criteria.get("city");
        Double latitude = (Double) criteria.get("latitude");
        Double longitude = (Double) criteria.get("longitude");
        return advertisements.stream()
                .filter(ad -> matches(ad, criteria))
                .collect(Collectors.toList());
    }

    @Override
    public boolean matches(Advertisement advertisement, Map<String, Object> criteria) {
        String countryCode = (String) criteria.get("countryCode");
        String region = (String) criteria.get("region");
        String city = (String) criteria.get("city");
        Double latitude = (Double) criteria.get("latitude");
        Double longitude = (Double) criteria.get("longitude");
        if (advertisement.getGeoTargets() == null || advertisement.getGeoTargets().isEmpty()) {
            return false;
        }
        boolean hasIncludeMatch = advertisement.getGeoTargets().stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));
        boolean hasExcludeMatch = advertisement.getGeoTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));
        return hasIncludeMatch && !hasExcludeMatch;
    }
}
