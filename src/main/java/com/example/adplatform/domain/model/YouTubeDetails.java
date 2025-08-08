package com.example.adplatform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value object holding details for YouTube-based advertisements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeDetails {
    /**
     * The YouTube video ID. For consistency, this should typically match Advertisement.sourceIdentifier
     * when Advertisement.source == AdvertisementSource.YOUTUBE.
     */
    private String videoId;

    /** Human-friendly video title. */
    private String videoTitle;

    /** Channel ID hosting the video (optional). */
    private String channelId;

    /** Channel title/name. */
    private String channelTitle;

    /** Duration in seconds (optional). */
    private Integer durationSeconds;

    /** Default or chosen thumbnail URL (optional). */
    private String thumbnailUrl;

    /** Publish datetime (optional). */
    private Instant publishedAt;
}
