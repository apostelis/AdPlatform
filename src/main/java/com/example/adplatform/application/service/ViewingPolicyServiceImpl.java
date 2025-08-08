package com.example.adplatform.application.service;

import com.example.adplatform.application.port.in.ViewingPolicyService;
import com.example.adplatform.domain.model.Advertisement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default viewing policy implementation.
 */
@Service
@Slf4j
public class ViewingPolicyServiceImpl implements ViewingPolicyService {

    private static final Comparator<Advertisement> WEIGHT_THEN_CREATED_DESC =
            Comparator.comparingInt(Advertisement::getWeight).reversed()
                    .thenComparing(Advertisement::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));

    @Override
    public List<Advertisement> orderForDisplay(List<Advertisement> advertisements, LocalDateTime now) {
        if (advertisements == null || advertisements.isEmpty()) {
            return List.of();
        }
        // Partition into override-active and others
        List<Advertisement> overrides = new ArrayList<>();
        List<Advertisement> others = new ArrayList<>();
        for (Advertisement ad : advertisements) {
            if (isOverrideActive(ad, now)) {
                overrides.add(ad);
            } else {
                others.add(ad);
            }
        }
        overrides = overrides.stream().sorted(WEIGHT_THEN_CREATED_DESC).collect(Collectors.toList());
        others = others.stream().sorted(WEIGHT_THEN_CREATED_DESC).collect(Collectors.toList());
        // Combine
        List<Advertisement> ordered = new ArrayList<>(overrides.size() + others.size());
        ordered.addAll(overrides);
        ordered.addAll(others);
        return ordered;
    }

    private boolean isOverrideActive(Advertisement ad, LocalDateTime now) {
        LocalDateTime start = ad.getOverrideStart();
        LocalDateTime end = ad.getOverrideEnd();
        if (start == null || end == null) return false;
        return (now.isEqual(start) || now.isAfter(start)) && (now.isBefore(end) || now.isEqual(end));
    }
}
