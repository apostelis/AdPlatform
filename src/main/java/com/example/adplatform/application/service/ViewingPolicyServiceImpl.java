package com.example.adplatform.application.service;

import com.example.adplatform.application.port.in.ViewingPolicyService;
import com.example.adplatform.domain.model.Advertisement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    // In-memory view counters per ad id, to provide deterministic fair distribution across calls
    private final ConcurrentMap<Long, Long> viewCounts = new ConcurrentHashMap<>();

    @Override
    public List<Advertisement> orderForDisplay(List<Advertisement> advertisements, LocalDateTime now) {
        if (advertisements == null || advertisements.isEmpty()) {
            return List.of();
        }
        // Partition into override-active and others
        var partitions = partitionByOverride(advertisements, now);
        List<Advertisement> overrides = partitions.overrides.stream().sorted(WEIGHT_THEN_CREATED_DESC).collect(Collectors.toList());
        List<Advertisement> others = partitions.others.stream().sorted(WEIGHT_THEN_CREATED_DESC).collect(Collectors.toList());
        // Combine
        List<Advertisement> ordered = new ArrayList<>(overrides.size() + others.size());
        ordered.addAll(overrides);
        ordered.addAll(others);
        return ordered;
    }

    @Override
    public Advertisement selectOneFair(List<Advertisement> advertisements, LocalDateTime now, Random random) {
        if (advertisements == null || advertisements.isEmpty()) return null;
        if (random == null) random = new Random();
        var partitions = partitionByOverride(advertisements, now);
        List<Advertisement> pool = !partitions.overrides.isEmpty() ? partitions.overrides : partitions.others;
        if (pool.isEmpty()) return null;
        long totalWeight = 0L;
        for (Advertisement ad : pool) {
            int w = Math.max(0, ad.getWeight());
            totalWeight += w;
        }
        if (totalWeight <= 0) {
            // Fallback to deterministic top
            return orderForDisplay(pool, now).get(0);
        }
        long r = (long) (random.nextDouble() * totalWeight);
        long cumulative = 0L;
        for (Advertisement ad : pool) {
            cumulative += Math.max(0, ad.getWeight());
            if (r < cumulative) {
                return ad;
            }
        }
        // Shouldn't reach here, but fallback to last
        return pool.get(pool.size() - 1);
    }

    @Override
    public List<Advertisement> orderForDisplayWithFairFirst(List<Advertisement> advertisements, LocalDateTime now, Random random) {
        List<Advertisement> ordered = orderForDisplay(advertisements, now);
        if (ordered.isEmpty()) return ordered;
        Advertisement fair = selectDeterministicFair(ordered, now);
        if (fair == null) return ordered;
        // increment view count for the selected one
        viewCounts.merge(fair.getId(), 1L, Long::sum);
        List<Advertisement> result = new ArrayList<>(ordered.size());
        result.add(fair);
        for (Advertisement ad : ordered) {
            if (!ad.equals(fair)) {
                result.add(ad);
            }
        }
        return result;
    }

    private Advertisement selectDeterministicFair(List<Advertisement> advertisements, LocalDateTime now) {
        var partitions = partitionByOverride(advertisements, now);
        List<Advertisement> pool = !partitions.overrides.isEmpty() ? partitions.overrides : partitions.others;
        if (pool.isEmpty()) return null;
        long totalWeight = pool.stream().mapToInt(a -> Math.max(0, a.getWeight())).sum();
        if (totalWeight <= 0) {
            return orderForDisplay(pool, now).get(0);
        }
        long totalViewsInPool = 0L;
        for (Advertisement ad : pool) {
            totalViewsInPool += viewCounts.getOrDefault(ad.getId(), 0L);
        }
        if (totalViewsInPool == 0L) {
            // First selection: highest weight, then createdAt desc
            return pool.stream().sorted(WEIGHT_THEN_CREATED_DESC).findFirst().orElse(null);
        }
        // Choose the most under-served ad by minimizing count_i / p_i
        Advertisement best = null;
        double bestScore = Double.POSITIVE_INFINITY;
        for (Advertisement ad : pool) {
            long count = viewCounts.getOrDefault(ad.getId(), 0L);
            double p = Math.max(0, ad.getWeight()) / (double) totalWeight;
            double score = p > 0 ? (count / p) : Double.POSITIVE_INFINITY;
            if (score < bestScore) {
                bestScore = score;
                best = ad;
            } else if (score == bestScore && best != null) {
                // tie-break: higher weight, then newer createdAt
                int cmp = Integer.compare(ad.getWeight(), best.getWeight());
                if (cmp > 0) {
                    best = ad;
                } else if (cmp == 0) {
                    LocalDateTime ca = ad.getCreatedAt();
                    LocalDateTime cb = best.getCreatedAt();
                    if (cb == null || (ca != null && ca.isAfter(cb))) {
                        best = ad;
                    }
                }
            }
        }
        if (best == null) {
            best = pool.get(0);
        }
        return best;
    }

    private static final class Partitions {
        final List<Advertisement> overrides;
        final List<Advertisement> others;
        Partitions(List<Advertisement> overrides, List<Advertisement> others) {
            this.overrides = overrides;
            this.others = others;
        }
    }

    private Partitions partitionByOverride(List<Advertisement> advertisements, LocalDateTime now) {
        List<Advertisement> overrides = new ArrayList<>();
        List<Advertisement> others = new ArrayList<>();
        for (Advertisement ad : advertisements) {
            if (isOverrideActive(ad, now)) {
                overrides.add(ad);
            } else {
                others.add(ad);
            }
        }
        return new Partitions(overrides, others);
    }

    private boolean isOverrideActive(Advertisement ad, LocalDateTime now) {
        LocalDateTime start = ad.getOverrideStart();
        LocalDateTime end = ad.getOverrideEnd();
        if (start == null || end == null) return false;
        return (now.isEqual(start) || now.isAfter(start)) && (now.isBefore(end) || now.isEqual(end));
    }
}
