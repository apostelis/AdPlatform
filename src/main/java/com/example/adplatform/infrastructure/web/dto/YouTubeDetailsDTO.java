package com.example.adplatform.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for YouTube details exposed via the REST API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeDetailsDTO {
    private String videoId;
    private String videoTitle;
    private String channelId;
    private String channelTitle;
    private Integer durationSeconds;
    private String thumbnailUrl;
    private Instant publishedAt;
}
