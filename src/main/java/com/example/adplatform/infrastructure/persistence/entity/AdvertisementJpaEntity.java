package com.example.adplatform.infrastructure.persistence.entity;

import com.example.adplatform.domain.model.AdvertisementSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing an Advertisement in the database.
 * This is an infrastructure entity in the hexagonal architecture.
 */
@Entity
@Table(name = "advertisements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @NotBlank
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AdvertisementSource source;

    @Column(nullable = false)
    private String sourceIdentifier; // File path or YouTube ID

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active;

    @Embedded
    private YouTubeDetailsEmbeddable youtubeDetails;

    // Targeting rules
    @ElementCollection
    @CollectionTable(name = "advertisement_geo_targets", joinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<GeoTargetJpaEntity> geoTargets = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "advertisement_bio_targets", joinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<BioTargetJpaEntity> bioTargets = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "advertisement_mood_targets", joinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<MoodTargetJpaEntity> moodTargets = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}