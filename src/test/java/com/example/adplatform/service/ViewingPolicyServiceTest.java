package com.example.adplatform.service;

import com.example.adplatform.application.service.ViewingPolicyServiceImpl;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ViewingPolicyServiceTest {

    private ViewingPolicyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ViewingPolicyServiceImpl();
    }

    private Advertisement ad(long id, int weight, LocalDateTime createdAt,
                             LocalDateTime overrideStart, LocalDateTime overrideEnd) {
        return Advertisement.builder()
                .id(id)
                .title("Ad " + id)
                .description("desc")
                .content("content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("file.mp4")
                .active(true)
                .weight(weight)
                .createdAt(createdAt)
                .overrideStart(overrideStart)
                .overrideEnd(overrideEnd)
                .build();
    }

    @Test
    void orderForDisplay_ShouldReturnEmpty_OnNullOrEmpty() {
        assertNotNull(service.orderForDisplay(null));
        assertEquals(0, service.orderForDisplay(null).size());
        assertEquals(0, service.orderForDisplay(List.of()).size());
    }

    @Test
    void overrideAdsComeFirst_ThenWeightDesc_ThenCreatedAtDesc() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 8, 12, 0);

        // Non override ads
        Advertisement n1 = ad(1, 5, now.minusDays(2), null, null);
        Advertisement n2 = ad(2, 10, now.minusDays(3), null, null);
        Advertisement n3 = ad(3, 10, now.minusDays(1), null, null); // same weight as n2 but newer

        // Override-active ads
        Advertisement o2 = ad(4, 20, now.minusDays(2), now.minusDays(1), now.plusDays(1));
        Advertisement o3 = ad(5, 20, now.minusDays(5), now.minusDays(1), now.plusDays(1)); // tie with o2, newer createdAt
        Advertisement o1 = ad(6, 1, now.minusDays(10), now.minusHours(1), now.plusHours(1));

        List<Advertisement> input = List.of(n1, n2, n3, o1, o2, o3);
        List<Advertisement> ordered = service.orderForDisplay(input, now);

        // Overrides first, ordered by weight desc then createdAt desc (newer first)
        assertEquals(List.of(4L, 5L, 6L, 3L, 2L, 1L), ordered.stream().map(Advertisement::getId).toList());
    }

    @Test
    void nonOverrideAdsOrderedByWeightDesc_ThenCreatedAtDesc() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 8, 12, 0);
        Advertisement a = ad(1, 3, now.minusDays(1), null, null);
        Advertisement b = ad(2, 5, now.minusDays(2), null, null);
        Advertisement c = ad(3, 6, now.minusHours(3), null, null); // same weight as b, newer createdAt

        List<Advertisement> ordered = service.orderForDisplay(List.of(a, b, c), now);
        assertEquals(List.of(3L, 2L, 1L), ordered.stream().map(Advertisement::getId).toList());
    }

    @Test
    void overrideWindowEdgeInclusive() {
        LocalDateTime start = LocalDateTime.of(2025, 8, 8, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 8, 12, 0);
        Advertisement o = ad(1, 1, start, start, end);
        Advertisement n = ad(2, 100, start, null, null);

        // At start - override should be active
        List<Advertisement> atStart = service.orderForDisplay(List.of(n, o), start);
        assertEquals(List.of(1L, 2L), atStart.stream().map(Advertisement::getId).toList());

        // At end - override should still be active
        List<Advertisement> atEnd = service.orderForDisplay(List.of(n, o), end);
        assertEquals(List.of(1L, 2L), atEnd.stream().map(Advertisement::getId).toList());
    }

    @Test
    void nullCreatedAtHandledInOrdering() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 8, 12, 0);
        Advertisement a = ad(1, 5, null, null, null);
        Advertisement b = ad(2, 5, now.minusDays(1), null, null);
        Advertisement c = ad(3, 10, null, null, null);

        List<Advertisement> ordered = service.orderForDisplay(List.of(a, b, c), now);
        // Weight 10 first, then among weight 5, one with non-null createdAt newer should come before null
        assertEquals(List.of(3L, 2L, 1L), ordered.stream().map(Advertisement::getId).toList());
    }

    @Test
    void mixedOverridesAndNonOverrides_WithSameWeight_UsesCreatedAtDescWithinGroups() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 8, 12, 0);
        Advertisement oOld = ad(1, 10, now.minusDays(3), now.minusHours(1), now.plusHours(1));
        Advertisement oNew = ad(2, 10, now.minusHours(2), now.minusHours(1), now.plusHours(1));
        Advertisement nOld = ad(3, 5, now.minusDays(2), null, null);
        Advertisement nNew = ad(4, 5, now.minusHours(1), null, null);

        List<Advertisement> ordered = service.orderForDisplay(List.of(nOld, oOld, oNew, nNew), now);
        assertEquals(List.of(2L, 1L, 4L, 3L), ordered.stream().map(Advertisement::getId).toList());
    }
}
