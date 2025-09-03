package com.example.adplatform.infrastructure.persistence.adapter;

import com.example.adplatform.domain.model.*;
import com.example.adplatform.infrastructure.persistence.entity.*;
import com.example.adplatform.infrastructure.persistence.entity.MoodTargetJpaEntity.MoodJpaEnum;
import com.example.adplatform.infrastructure.persistence.repository.AdvertisementJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementRepositoryAdapterTest {

    @Mock
    private AdvertisementJpaRepository jpaRepository;

    @InjectMocks
    private AdvertisementRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<AdvertisementJpaEntity> entityCaptor;

    private Advertisement domainAdvertisement;
    private AdvertisementJpaEntity jpaEntity;

    @BeforeEach
    void setUp() {
        // Create test domain advertisement
        domainAdvertisement = Advertisement.builder()
                .id(1L)
                .title("Test Advertisement")
                .description("Test Description")
                .content("Test Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("/test/path")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .geoTargets(Set.of(
                        GeoTarget.builder()
                                .countryCode("US")
                                .region("NY")
                                .city("New York")
                                .include(true)
                                .build(),
                        GeoTarget.builder()
                                .countryCode("CA")
                                .region("CA")
                                .city("Los Angeles")
                                .include(true)
                                .build()
                ))
                .bioTargets(Set.of(
                        BioTarget.builder()
                                .minAge(18)
                                .maxAge(34)
                                .gender(Gender.MALE)
                                .occupation("engineer")
                                .educationLevel("bachelor")
                                .language("en")
                                .interestCategory("technology")
                                .include(true)
                                .build(),
                        BioTarget.builder()
                                .minAge(25)
                                .maxAge(54)
                                .gender(Gender.FEMALE)
                                .occupation("doctor")
                                .educationLevel("master")
                                .language("es")
                                .interestCategory("health")
                                .include(true)
                                .build()
                ))
                .moodTargets(Set.of(
                        MoodTarget.builder()
                                .mood(Mood.HAPPY)
                                .intensityMin(1)
                                .intensityMax(5)
                                .timeOfDay("morning")
                                .dayOfWeek("monday")
                                .season("spring")
                                .include(true)
                                .build(),
                        MoodTarget.builder()
                                .mood(Mood.EXCITED)
                                .intensityMin(6)
                                .intensityMax(10)
                                .timeOfDay("evening")
                                .dayOfWeek("friday")
                                .season("summer")
                                .include(true)
                                .build()
                ))
                .build();

        // Create test JPA entity
        jpaEntity = new AdvertisementJpaEntity();
        jpaEntity.setId(1L);
        jpaEntity.setTitle("Test Advertisement");
        jpaEntity.setDescription("Test Description");
        jpaEntity.setContent("Test Content");
        jpaEntity.setSource(AdvertisementSource.STORAGE);
        jpaEntity.setSourceIdentifier("/test/path");
        jpaEntity.setCreatedAt(LocalDateTime.now());
        jpaEntity.setUpdatedAt(LocalDateTime.now());
        jpaEntity.setActive(true);
    }

    @Test
    void save_shouldConvertAndSaveAdvertisement() {
        // Given
        when(jpaRepository.save(any(AdvertisementJpaEntity.class))).thenReturn(jpaEntity);

        // When
        Advertisement result = adapter.save(domainAdvertisement);

        // Then
        verify(jpaRepository).save(any(AdvertisementJpaEntity.class));
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Advertisement");
    }

    @Test
    void findById_whenExists_shouldReturnAdvertisement() {
        // Given
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(jpaEntity));

        // When
        Optional<Advertisement> result = adapter.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Advertisement");
        verify(jpaRepository).findById(1L);
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        // Given
        when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Advertisement> result = adapter.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(jpaRepository).findById(999L);
    }

    @Test
    void findAll_shouldReturnAllAdvertisements() {
        // Given
        List<AdvertisementJpaEntity> entities = List.of(jpaEntity);
        when(jpaRepository.findAll()).thenReturn(entities);

        // When
        List<Advertisement> result = adapter.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Advertisement");
        verify(jpaRepository).findAll();
    }

    @Test
    void findAllWithPageable_shouldReturnPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<AdvertisementJpaEntity> page = new PageImpl<>(List.of(jpaEntity), pageable, 1);
        when(jpaRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Advertisement> result = adapter.findAll(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Advertisement");
        verify(jpaRepository).findAll(pageable);
    }

    @Test
    void findByActiveTrue_shouldReturnOnlyActiveAdvertisements() {
        // Given
        List<AdvertisementJpaEntity> activeEntities = List.of(jpaEntity);
        when(jpaRepository.findByActiveTrue()).thenReturn(activeEntities);

        // When
        List<Advertisement> result = adapter.findByActiveTrue();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
        verify(jpaRepository).findByActiveTrue();
    }

    @Test
    void findBySource_shouldReturnAdvertisementsBySource() {
        // Given
        List<AdvertisementJpaEntity> entities = List.of(jpaEntity);
        when(jpaRepository.findBySource(AdvertisementSource.STORAGE)).thenReturn(entities);

        // When
        List<Advertisement> result = adapter.findBySource(AdvertisementSource.STORAGE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSource()).isEqualTo(AdvertisementSource.STORAGE);
        verify(jpaRepository).findBySource(AdvertisementSource.STORAGE);
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnMatchingAdvertisements() {
        // Given
        List<AdvertisementJpaEntity> entities = List.of(jpaEntity);
        when(jpaRepository.findByTitleContainingIgnoreCase("test")).thenReturn(entities);

        // When
        List<Advertisement> result = adapter.findByTitleContainingIgnoreCase("test");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).containsIgnoringCase("test");
        verify(jpaRepository).findByTitleContainingIgnoreCase("test");
    }

    @Test
    void deleteById_shouldDeleteAdvertisement() {
        // When
        adapter.deleteById(1L);

        // Then
        verify(jpaRepository).deleteById(1L);
    }

    @Test
    void findByCountryCode_shouldReturnAdvertisementsForCountry() {
        // Given
        List<AdvertisementJpaEntity> entities = List.of(jpaEntity);
        when(jpaRepository.findByCountryCode("US")).thenReturn(entities);

        // When
        List<Advertisement> result = adapter.findByCountryCode("US");

        // Then
        assertThat(result).hasSize(1);
        verify(jpaRepository).findByCountryCode("US");
    }

    @Test
    void findByAgeRange_shouldReturnAdvertisementsForAge() {
        // Given
        List<AdvertisementJpaEntity> entities = List.of(jpaEntity);
        when(jpaRepository.findByAgeRange(25)).thenReturn(entities);

        // When
        List<Advertisement> result = adapter.findByAgeRange(25);

        // Then
        assertThat(result).hasSize(1);
        verify(jpaRepository).findByAgeRange(25);
    }

    @Test
    void findByMood_shouldReturnAdvertisementsForMood() {
        // Given
        List<AdvertisementJpaEntity> entities = List.of(jpaEntity);
        when(jpaRepository.findByMood(MoodJpaEnum.HAPPY)).thenReturn(entities);

        // When
        List<Advertisement> result = adapter.findByMood(Mood.HAPPY);

        // Then
        assertThat(result).hasSize(1);
        verify(jpaRepository).findByMood(MoodJpaEnum.HAPPY);
    }

    @Test
    void testEmptyCollectionsAreNotPersisted() {
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