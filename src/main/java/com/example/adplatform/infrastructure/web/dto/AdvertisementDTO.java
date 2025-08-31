package com.example.adplatform.infrastructure.web.dto;

import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.infrastructure.web.validation.ValidClickableAdvertisement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

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
@ValidClickableAdvertisement
public class AdvertisementDTO {

    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;
    
    @NotNull(message = "Source is required")
    private AdvertisementSource source;
    
    @NotBlank(message = "Source identifier is required")
    @Size(min = 1, max = 255, message = "Source identifier must be between 1 and 255 characters")
    private String sourceIdentifier;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // Click behavior
    @URL(message = "Target URL must be a valid URL")
    private String targetUrl;
    
    private boolean clickable;
    
    // Viewing policy
    @Min(value = 1, message = "Weight must be at least 1")
    @Max(value = 100, message = "Weight must not exceed 100")
    private int weight;
    
    private LocalDateTime overrideStart;
    private LocalDateTime overrideEnd;
    
    @Valid
    private YouTubeDetailsDTO youtubeDetails;
    
    @Valid
    @Default
    private Set<GeoTargetDTO> geoTargets = new HashSet<>();
    
    @Valid
    @Default
    private Set<BioTargetDTO> bioTargets = new HashSet<>();
    
    @Valid
    @Default
    private Set<MoodTargetDTO> moodTargets = new HashSet<>();
}