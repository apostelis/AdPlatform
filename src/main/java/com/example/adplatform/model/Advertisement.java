package com.example.adplatform.model;

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
 * Entity representing an Advertisement in the system.
 * Advertisements can be sourced from storage or YouTube and have
 * targeting rules based on geolocation, biographical data, and user mood.
 */
@Entity
@Table(name = "advertisements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {

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

    // Targeting rules
    @ElementCollection
    @CollectionTable(name = "advertisement_geo_targets", joinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<GeoTarget> geoTargets = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "advertisement_bio_targets", joinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<BioTarget> bioTargets = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "advertisement_mood_targets", joinColumns = @JoinColumn(name = "advertisement_id"))
    private Set<MoodTarget> moodTargets = new HashSet<>();

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