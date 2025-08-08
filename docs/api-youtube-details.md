# API Update: YouTube Details for Advertisements

Date: 2025-08-08

This update introduces YouTube details for advertisements whose source is YOUTUBE. The goal is to enrich ad payloads with metadata commonly used on the frontend without requiring an additional call.

## Backend changes
- Domain: Added YouTubeDetails value object and optional field `youtubeDetails` to `Advertisement` aggregate.
- Persistence: Added `YouTubeDetailsEmbeddable` and embedded it in `AdvertisementJpaEntity`.
- Web DTOs: Added `YouTubeDetailsDTO` and optional field `youtubeDetails` in `AdvertisementDTO`.
- Mapping: Updated AdvertisementRepositoryAdapter and AdvertisementMapper to map the new object across layers.
- Validation: If `source == YOUTUBE` and `youtubeDetails.videoId` is provided, it must match `sourceIdentifier`.

## API shape (excerpt)
AdvertisementDTO now contains an optional field when source is YOUTUBE:

```
{
  "id": 1,
  "title": "...",
  "source": "YOUTUBE",
  "sourceIdentifier": "<videoId>",
  "youtubeDetails": {
    "videoId": "<videoId>",
    "videoTitle": "...",
    "channelId": "...",
    "channelTitle": "...",
    "durationSeconds": 123,
    "thumbnailUrl": "https://...",
    "publishedAt": "2025-08-08T15:00:00Z"
  },
  ...
}
```

Notes:
- All fields in `youtubeDetails` are optional. If provided, `videoId` should match `sourceIdentifier`.
- No external API integration is introduced by this change; details are accepted/stored/returned if supplied.

## Frontend
- Added `YouTubeDetails` interface and optional `youtubeDetails` in `Advertisement` type.
- UI may render additional YouTube metadata when present.
- Details Card now renders a YouTube preview when source is YOUTUBE:
  - If a videoId is available (from youtubeDetails.videoId or sourceIdentifier), an embedded responsive 16:9 iframe is shown.
  - If no videoId but a thumbnailUrl exists, the thumbnail is displayed as a preview.

## Testing considerations
- Existing tests should pass. Add mapper tests if coverage policies require it.
