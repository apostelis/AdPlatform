package com.example.adplatform.service;

import com.example.adplatform.application.service.ViewingPolicyServiceImpl;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ViewingPolicyFairnessTest {

    private Advertisement ad(long id, int weight) {
        return Advertisement.builder()
                .id(id)
                .title("Ad " + id)
                .description("desc")
                .content("content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("file.mp4")
                .active(true)
                .weight(weight)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void selectOneFair_ShouldApproximateWeightProportions() {
        ViewingPolicyServiceImpl service = new ViewingPolicyServiceImpl();
        List<Advertisement> ads = List.of(
                ad(1, 50),
                ad(2, 100)
        );
        LocalDateTime now = LocalDateTime.of(2025,8,8,12,0);
        Random rng = new Random(12345);
        int trials = 20000;
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < trials; i++) {
            var chosen = service.selectOneFair(ads, now, rng);
            if (chosen.getId() == 1L) count1++;
            else if (chosen.getId() == 2L) count2++;
        }
        double p1 = count1 / (double) trials;
        double p2 = count2 / (double) trials;
        // Expected: ~0.333 and ~0.666 with a tolerance
        assertTrue(Math.abs(p1 - (50.0/150.0)) < 0.03, "Ad1 proportion out of tolerance: " + p1);
        assertTrue(Math.abs(p2 - (100.0/150.0)) < 0.03, "Ad2 proportion out of tolerance: " + p2);
    }

    @Test
    void orderForDisplayWithFairFirst_ShouldPlaceFairSelectionFirst() {
        ViewingPolicyServiceImpl service = new ViewingPolicyServiceImpl();
        List<Advertisement> ads = new ArrayList<>();
        ads.add(ad(1, 10));
        ads.add(ad(2, 20));
        ads.add(ad(3, 30));
        LocalDateTime now = LocalDateTime.of(2025,8,8,12,0);

        // First call should return highest weight first
        List<Advertisement> firstCall = service.orderForDisplayWithFairFirst(ads, now, new Random(7));
        assertEquals(3L, firstCall.getFirst().getId());

        // Now repeat many calls and ensure proportions follow weights within 10% tolerance
        int trials = 1000;
        int c1 = 0, c2 = 0, c3 = 0;
        for (int i = 0; i < trials; i++) {
            List<Advertisement> ff = service.orderForDisplayWithFairFirst(ads, now, new Random(7));
            long id = ff.get(0).getId();
            if (id == 1L) c1++; else if (id == 2L) c2++; else if (id == 3L) c3++;
        }
        double p1 = c1 / (double) trials;
        double p2 = c2 / (double) trials;
        double p3 = c3 / (double) trials;
        double w1 = 10.0 / 60.0;
        double w2 = 20.0 / 60.0;
        double w3 = 30.0 / 60.0;
        double tol = 0.10;
        assertTrue(Math.abs(p1 - w1) <= tol, "Ad1 outside tolerance: " + p1);
        assertTrue(Math.abs(p2 - w2) <= tol, "Ad2 outside tolerance: " + p2);
        assertTrue(Math.abs(p3 - w3) <= tol, "Ad3 outside tolerance: " + p3);
    }
}
