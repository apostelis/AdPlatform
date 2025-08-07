package com.example.adplatform.config;

import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.domain.model.Advertisement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for the DevDataInitializer class.
 * This test verifies that test data is properly loaded when the dev profile is active.
 */
@SpringBootTest
@ActiveProfiles("dev")
public class DevDataInitializerTest {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    /**
     * Test that verifies that test data is loaded when the dev profile is active.
     */
    @Test
    public void testDataIsLoaded() {
        // Get all advertisements
        List<Advertisement> advertisements = advertisementRepository.findAll();
        
        // Verify that advertisements were loaded
        assertFalse(advertisements.isEmpty(), "Test data should be loaded");
        
        // Verify that we have at least the basic types of advertisements
        assertTrue(advertisements.stream().anyMatch(ad -> ad.getTitle().contains("Basic")), 
                "Basic advertisement should be loaded");
        
        assertTrue(advertisements.stream().anyMatch(ad -> ad.getTitle().contains("US Targeted")), 
                "Geo-targeted advertisement should be loaded");
        
        assertTrue(advertisements.stream().anyMatch(ad -> ad.getTitle().contains("Young Adults")), 
                "Bio-targeted advertisement should be loaded");
        
        assertTrue(advertisements.stream().anyMatch(ad -> ad.getTitle().contains("Happy Mood")), 
                "Mood-targeted advertisement should be loaded");
        
        assertTrue(advertisements.stream().anyMatch(ad -> ad.getTitle().contains("YouTube")), 
                "YouTube advertisement should be loaded");
        
        assertTrue(advertisements.stream().anyMatch(ad -> ad.getTitle().contains("Inactive")), 
                "Inactive advertisement should be loaded");
        
        System.out.println("[DEBUG_LOG] Loaded " + advertisements.size() + " advertisements");
        advertisements.forEach(ad -> System.out.println("[DEBUG_LOG] Advertisement: " + ad.getTitle()));
    }
}