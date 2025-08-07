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

        // Create an advertisement with US geo targeting
        Advertisement usGeoTargetedAd = Advertisement.builder()
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
        
        advertisementRepository.save(usGeoTargetedAd);
        log.info("Created US geo-targeted advertisement: {}", usGeoTargetedAd.getTitle());
        
        // Create an advertisement with European geo targeting
        Advertisement euGeoTargetedAd = Advertisement.builder()
                .title("European Targeted Advertisement")
                .description("An advertisement targeted at European users")
                .content("This advertisement is only shown to users in Europe.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/eu-targeted.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .geoTargets(createEuropeanGeoTargets())
                .build();
        
        advertisementRepository.save(euGeoTargetedAd);
        log.info("Created European geo-targeted advertisement: {}", euGeoTargetedAd.getTitle());
        
        // Create an advertisement with Asia-Pacific geo targeting
        Advertisement apacGeoTargetedAd = Advertisement.builder()
                .title("Asia-Pacific Targeted Advertisement")
                .description("An advertisement targeted at Asia-Pacific users")
                .content("This advertisement is only shown to users in the Asia-Pacific region.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/apac-targeted.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .geoTargets(createAsiaPacificGeoTargets())
                .build();
        
        advertisementRepository.save(apacGeoTargetedAd);
        log.info("Created Asia-Pacific geo-targeted advertisement: {}", apacGeoTargetedAd.getTitle());
        
        // Create an advertisement with major cities geo targeting
        Advertisement citiesGeoTargetedAd = Advertisement.builder()
                .title("Major Cities Targeted Advertisement")
                .description("An advertisement targeted at users in major global cities")
                .content("This advertisement is only shown to users in major global cities.")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/ads/cities-targeted.mp4")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .geoTargets(createMajorCitiesGeoTargets())
                .build();
        
        advertisementRepository.save(citiesGeoTargetedAd);
        log.info("Created major cities geo-targeted advertisement: {}", citiesGeoTargetedAd.getTitle());

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
                .countryCode("US")
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
        
        // Target morning time with happy mood
        moodTargets.add(MoodTarget.builder()
                .mood(Mood.HAPPY)
                .timeOfDay("MORNING")
                .include(true)
                .build());
        
        // Target weekend with happy mood
        moodTargets.add(MoodTarget.builder()
                .mood(Mood.HAPPY)
                .dayOfWeek("WEEKEND")
                .include(true)
                .build());
        
        return moodTargets;
    }
    
    /**
     * Creates a set of geo targets for European countries.
     *
     * @return Set of geo targets
     */
    private Set<GeoTarget> createEuropeanGeoTargets() {
        Set<GeoTarget> geoTargets = new HashSet<>();
        
        // Include major European countries
        geoTargets.add(GeoTarget.builder()
                .countryCode("UK")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("DE")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("FR")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("IT")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("ES")
                .include(true)
                .build());
        
        // Include London with radius
        geoTargets.add(GeoTarget.builder()
                .city("London")
                .countryCode("UK")
                .latitude(51.5074)
                .longitude(-0.1278)
                .radiusKm(30)
                .include(true)
                .build());
        
        // Exclude specific regions
        geoTargets.add(GeoTarget.builder()
                .countryCode("RU")
                .include(false)
                .build());
        
        return geoTargets;
    }
    
    /**
     * Creates a set of geo targets for Asia-Pacific countries.
     *
     * @return Set of geo targets
     */
    private Set<GeoTarget> createAsiaPacificGeoTargets() {
        Set<GeoTarget> geoTargets = new HashSet<>();
        
        // Include major APAC countries
        geoTargets.add(GeoTarget.builder()
                .countryCode("JP")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("CN")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("AU")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("SG")
                .include(true)
                .build());
                
        geoTargets.add(GeoTarget.builder()
                .countryCode("IN")
                .include(true)
                .build());
        
        // Include Tokyo with radius
        geoTargets.add(GeoTarget.builder()
                .city("Tokyo")
                .countryCode("JP")
                .latitude(35.6762)
                .longitude(139.6503)
                .radiusKm(50)
                .include(true)
                .build());
        
        // Include Sydney with radius
        geoTargets.add(GeoTarget.builder()
                .city("Sydney")
                .countryCode("AU")
                .latitude(-33.8688)
                .longitude(151.2093)
                .radiusKm(40)
                .include(true)
                .build());
        
        return geoTargets;
    }
    
    /**
     * Creates a set of geo targets for major global cities.
     *
     * @return Set of geo targets
     */
    private Set<GeoTarget> createMajorCitiesGeoTargets() {
        Set<GeoTarget> geoTargets = new HashSet<>();
        
        // New York
        geoTargets.add(GeoTarget.builder()
                .city("New York")
                .countryCode("US")
                .latitude(40.7128)
                .longitude(-74.0060)
                .radiusKm(50)
                .include(true)
                .build());
        
        // London
        geoTargets.add(GeoTarget.builder()
                .city("London")
                .countryCode("UK")
                .latitude(51.5074)
                .longitude(-0.1278)
                .radiusKm(50)
                .include(true)
                .build());
        
        // Tokyo
        geoTargets.add(GeoTarget.builder()
                .city("Tokyo")
                .countryCode("JP")
                .latitude(35.6762)
                .longitude(139.6503)
                .radiusKm(50)
                .include(true)
                .build());
        
        // Paris
        geoTargets.add(GeoTarget.builder()
                .city("Paris")
                .countryCode("FR")
                .latitude(48.8566)
                .longitude(2.3522)
                .radiusKm(40)
                .include(true)
                .build());
        
        // Sydney
        geoTargets.add(GeoTarget.builder()
                .city("Sydney")
                .countryCode("AU")
                .latitude(-33.8688)
                .longitude(151.2093)
                .radiusKm(40)
                .include(true)
                .build());
        
        // Singapore
        geoTargets.add(GeoTarget.builder()
                .city("Singapore")
                .countryCode("SG")
                .latitude(1.3521)
                .longitude(103.8198)
                .radiusKm(30)
                .include(true)
                .build());
        
        // Dubai
        geoTargets.add(GeoTarget.builder()
                .city("Dubai")
                .countryCode("AE")
                .latitude(25.2048)
                .longitude(55.2708)
                .radiusKm(40)
                .include(true)
                .build());
        
        return geoTargets;
    }
}