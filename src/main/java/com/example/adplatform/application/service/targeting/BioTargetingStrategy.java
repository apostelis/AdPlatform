package com.example.adplatform.application.service.targeting;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.Gender;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BioTargetingStrategy implements TargetingStrategy {

    public static final String KEY = "bio";

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
    @SuppressWarnings("unchecked")
    public boolean matches(Advertisement advertisement, Map<String, Object> criteria) {
        Integer age = (Integer) criteria.get("age");
        String genderStr = (String) criteria.get("gender");
        String occupation = (String) criteria.get("occupation");
        String educationLevel = (String) criteria.get("educationLevel");
        String language = (String) criteria.get("language");
        Set<String> interests;
        Object interestsObj = criteria.get("interests");
        if (interestsObj instanceof Set) {
            interests = (Set<String>) interestsObj;
        } else if (interestsObj instanceof List) {
            interests = new HashSet<>((List<String>) interestsObj);
        } else {
            interests = Collections.emptySet();
        }

        if (advertisement.getBioTargets() == null || advertisement.getBioTargets().isEmpty()) {
            return false;
        }
        final Gender gender = genderStr != null ? Gender.fromString(genderStr) : null;
        boolean hasIncludeMatch = advertisement.getBioTargets().stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(age, gender, occupation, educationLevel, language, interests));
        boolean hasExcludeMatch = advertisement.getBioTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(age, gender, occupation, educationLevel, language, interests));
        return hasIncludeMatch && !hasExcludeMatch;
    }
}
