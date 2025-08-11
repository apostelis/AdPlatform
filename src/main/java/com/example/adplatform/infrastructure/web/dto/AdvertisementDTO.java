package com.example.adplatform.infrastructure.web.dto;

import com.example.adplatform.domain.model.AdvertisementSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Advertisement.
 * This is used for communication between the controller and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {

    private Long id;
    private String title;
    private String description;
    private String content;
    private AdvertisementSource source;
    private String sourceIdentifier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    // Click behavior
    private String targetUrl;
    private boolean clickable;
    // Viewing policy
    private int weight;
    private LocalDateTime overrideStart;
    private LocalDateTime overrideEnd;
    private YouTubeDetailsDTO youtubeDetails;
    private Set<GeoTargetDTO> geoTargets = new HashSet<>();
    private Set<BioTargetDTO> bioTargets = new HashSet<>();
    private Set<MoodTargetDTO> moodTargets = new HashSet<>();
}