package com.example.adplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Embeddable JPA component storing YouTube details for advertisements.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeDetailsEmbeddable {

    private String videoId;

    private String videoTitle;

    private String channelId;

    private String channelTitle;

    private Integer durationSeconds;

    private String thumbnailUrl;

    @Column(name = "youtube_published_at")
    private Instant publishedAt;
}
