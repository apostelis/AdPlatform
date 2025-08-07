package com.example.adplatform.infrastructure.persistence.adapter;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.infrastructure.persistence.entity.AdvertisementJpaEntity;
import com.example.adplatform.infrastructure.persistence.repository.AdvertisementJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvertisementRepositoryAdapterTest {

    @Mock
    private AdvertisementJpaRepository jpaRepository;

    @InjectMocks
    private AdvertisementRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<AdvertisementJpaEntity> entityCaptor;

    @Test
    public void testEmptyCollectionsAreNotPersisted() {
        // Create an advertisement with empty collections
        Advertisement advertisement = Advertisement.builder()
                .title("Test Advertisement")
                .description("Test Description")
                .content("Test Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/test/path")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .geoTargets(new HashSet<>()) // Empty collection
                .bioTargets(new HashSet<>()) // Empty collection
                .moodTargets(new HashSet<>()) // Empty collection
                .build();

        // Mock the repository save method
        AdvertisementJpaEntity savedEntity = new AdvertisementJpaEntity();
        when(jpaRepository.save(entityCaptor.capture())).thenReturn(savedEntity);

        // Call the adapter save method
        adapter.save(advertisement);

        // Verify that the entity passed to the repository has null collections
        AdvertisementJpaEntity capturedEntity = entityCaptor.getValue();
        assertNull(capturedEntity.getGeoTargets(), "GeoTargets should be null for empty collections");
        assertNull(capturedEntity.getBioTargets(), "BioTargets should be null for empty collections");
        assertNull(capturedEntity.getMoodTargets(), "MoodTargets should be null for empty collections");
    }
}