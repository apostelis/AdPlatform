package com.example.adplatform.application.service.targeting;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.Mood;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MoodTargetingStrategy implements TargetingStrategy {

    public static final String KEY = "mood";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<Advertisement> filter(List<Advertisement> advertisements, Map<String, Object> criteria) {
        if (advertisements == null || advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        return advertisements.stream()
                .filter(ad -> matches(ad, criteria))
                .collect(Collectors.toList());
    }

    @Override
    public boolean matches(Advertisement advertisement, Map<String, Object> criteria) {
        Mood mood = (Mood) criteria.get("mood");
        Integer intensity = (Integer) criteria.get("intensity");
        String timeOfDay = (String) criteria.get("timeOfDay");
        String dayOfWeek = (String) criteria.get("dayOfWeek");
        String season = (String) criteria.get("season");

        if (advertisement.getMoodTargets() == null || advertisement.getMoodTargets().isEmpty()) {
            return false;
        }
        boolean hasIncludeMatch = advertisement.getMoodTargets().stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));
        boolean hasExcludeMatch = advertisement.getMoodTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));
        return hasIncludeMatch && !hasExcludeMatch;
    }
}
