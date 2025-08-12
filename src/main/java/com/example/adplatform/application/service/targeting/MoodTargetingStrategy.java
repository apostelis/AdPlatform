package com.example.adplatform.application.service.targeting;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.Mood;
import com.example.adplatform.domain.model.MoodTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MoodTargetingStrategy implements TargetingStrategy {

    public static final String KEY = "mood";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<Advertisement> filter(List<Advertisement> advertisements, Map<String, Object> criteria) {
        if (advertisements == null || advertisements.isEmpty()) {
            log.debug("Mood targeting: no advertisements to filter");
            return Collections.emptyList();
        }
        List<Advertisement> result = advertisements.stream()
                .filter(ad -> matches(ad, criteria))
                .collect(Collectors.toList());
        log.debug("Mood targeting: filtered {} out of {} advertisements", result.size(), advertisements.size());
        return result;
    }

    @Override
    public boolean matches(Advertisement advertisement, Map<String, Object> criteria) {
        Mood mood = (Mood) criteria.get("mood");
        Integer intensity = (Integer) criteria.get("intensity");
        String timeOfDay = (String) criteria.get("timeOfDay");
        String dayOfWeek = (String) criteria.get("dayOfWeek");
        String season = (String) criteria.get("season");

        if (advertisement.getMoodTargets() == null || advertisement.getMoodTargets().isEmpty()) {
            log.trace("Mood targeting: advertisement {} has no mood targets", advertisement.getId());
            return false;
        }
        boolean hasIncludeMatch = advertisement.getMoodTargets().stream()
                .filter(MoodTarget::isInclude)
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));
        boolean hasExcludeMatch = advertisement.getMoodTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));
        boolean match = hasIncludeMatch && !hasExcludeMatch;
        log.trace("Mood targeting: advertisement {} match={}, includeMatch={}, excludeMatch={}", advertisement.getId(), match, hasIncludeMatch, hasExcludeMatch);
        return match;
    }
}
