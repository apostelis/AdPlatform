package com.example.adplatform.config;

import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration class for initializing test data in the development environment.
 * This class is only active when the "dev" profile is active.
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataInitializer {

    private final AdvertisementRepository advertisementRepository;

    /**
     * Creates a CommandLineRunner bean that initializes test data when the application starts.
     * This will only run when the "dev" profile is active.
     *
     * @return CommandLineRunner that initializes test data
     */
    @Bean
    public CommandLineRunner initTestData() {
        return args -> {
            log.info("Initializing test data for dev profile...");
            
            // Create and save test advertisements
            createTestAdvertisements();
            
            log.info("Test data initialization completed.");
        };
    }

    /**
     * Creates and saves test advertisements with various targeting criteria.
     */
    private void createTestAdvertisements() {
        // Create a basic advertisement with no targeting
        Advertisement basicAd = Advertisement.builder()
                .title("Basic Advertisement")
                .description("A simple advertisement with no targeting")
                .content("This is a basic advertisement content.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/basic.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
        
        advertisementRepository.save(basicAd);
        log.info("Created basic advertisement: {}", basicAd.getTitle());

        // Create an advertisement with geo targeting
        Advertisement geoTargetedAd = Advertisement.builder()
                .title("US Targeted Advertisement")
                .description("An advertisement targeted at US users")
                .content("This advertisement is only shown to users in the United States.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/us-targeted.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .geoTargets(createUsGeoTargets())
                .build();
        
        advertisementRepository.save(geoTargetedAd);
        log.info("Created geo-targeted advertisement: {}", geoTargetedAd.getTitle());

        // Create an advertisement with bio targeting
        Advertisement bioTargetedAd = Advertisement.builder()
                .title("Young Adults Advertisement")
                .description("An advertisement targeted at young adults")
                .content("This advertisement is targeted at young adults aged 18-30.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/young-adults.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .bioTargets(createYoungAdultBioTargets())
                .build();
        
        advertisementRepository.save(bioTargetedAd);
        log.info("Created bio-targeted advertisement: {}", bioTargetedAd.getTitle());

        // Create an advertisement with mood targeting
        Advertisement moodTargetedAd = Advertisement.builder()
                .title("Happy Mood Advertisement")
                .description("An advertisement for users in a happy mood")
                .content("This advertisement is shown to users who are in a happy mood.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/happy-mood.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .moodTargets(createHappyMoodTargets())
                .build();
        
        advertisementRepository.save(moodTargetedAd);
        log.info("Created mood-targeted advertisement: {}", moodTargetedAd.getTitle());

        // Create a YouTube advertisement
        Advertisement youtubeAd = Advertisement.builder()
                .title("YouTube Advertisement")
                .description("An advertisement sourced from YouTube")
                .content("This advertisement is sourced from YouTube.")
                .source(AdvertisementSource.YOUTUBE)
                .sourceIdentifier("dQw4w9WgXcQ") // YouTube video ID
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
        
        advertisementRepository.save(youtubeAd);
        log.info("Created YouTube advertisement: {}", youtubeAd.getTitle());

        // Create an inactive advertisement
        Advertisement inactiveAd = Advertisement.builder()
                .title("Inactive Advertisement")
                .description("An advertisement that is not active")
                .content("This advertisement is not active and should not be shown.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/inactive.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(false)
                .build();
        
        advertisementRepository.save(inactiveAd);
        log.info("Created inactive advertisement: {}", inactiveAd.getTitle());
    }

    /**
     * Creates a set of geo targets for the US.
     *
     * @return Set of geo targets
     */
    private Set<GeoTarget> createUsGeoTargets() {
        Set<GeoTarget> geoTargets = new HashSet<>();
        
        // Include US
        geoTargets.add(GeoTarget.builder()
                .countryCode("US")
                .include(true)
                .build());
        
        // Include New York City with radius
        geoTargets.add(GeoTarget.builder()
                .city("New York")
                .region("NY")
                .latitude(40.7128)
                .longitude(-74.0060)
                .radiusKm(50)
                .include(true)
                .build());
        
        // Exclude Alaska
        geoTargets.add(GeoTarget.builder()
                .countryCode("US")
                .region("AK")
                .include(false)
                .build());
        
        return geoTargets;
    }

    /**
     * Creates a set of bio targets for young adults.
     *
     * @return Set of bio targets
     */
    private Set<BioTarget> createYoungAdultBioTargets() {
        Set<BioTarget> bioTargets = new HashSet<>();
        
        // Target young adults
        bioTargets.add(BioTarget.builder()
                .minAge(18)
                .maxAge(30)
                .gender(Gender.ALL)
                .include(true)
                .build());
        
        // Target college students
        bioTargets.add(BioTarget.builder()
                .educationLevel("College")
                .include(true)
                .build());
        
        // Add interests for different categories
        bioTargets.add(BioTarget.builder()
                .interestCategory("Technology")
                .include(true)
                .build());
                
        bioTargets.add(BioTarget.builder()
                .interestCategory("Music")
                .include(true)
                .build());
                
        bioTargets.add(BioTarget.builder()
                .interestCategory("Travel")
                .include(true)
                .build());
        
        return bioTargets;
    }

    /**
     * Creates a set of mood targets for happy mood.
     *
     * @return Set of mood targets
     */
    private Set<MoodTarget> createHappyMoodTargets() {
        Set<MoodTarget> moodTargets = new HashSet<>();
        
        // Target happy mood
        moodTargets.add(MoodTarget.builder()
                .mood(Mood.HAPPY)
                .intensityMin(3)
                .intensityMax(10)
                .include(true)
                .build());
        
        // Target morning time
        moodTargets.add(MoodTarget.builder()
                .timeOfDay("MORNING")
                .include(true)
                .build());
        
        // Target weekend
        moodTargets.add(MoodTarget.builder()
                .dayOfWeek("WEEKEND")
                .include(true)
                .build());
        
        return moodTargets;
    }
}