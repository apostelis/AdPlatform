# Database Schema Documentation

This document provides an overview of the relational schema managed by Liquibase for the Advertisement Platform.

Schema versioning is defined in `src/main/resources/db/changelog/db.changelog-master.xml` and included change logs.

## Tables

### advertisements
- id (BIGINT, PK, auto-increment, not null)
- title (VARCHAR 255, not null)
- description (VARCHAR 1000, nullable)
- content (VARCHAR 255, not null)
- source (VARCHAR 50, not null)
- source_identifier (VARCHAR 255, not null)
- created_at (TIMESTAMP, not null)
- updated_at (TIMESTAMP, not null)
- active (BOOLEAN, not null)
- clickable (BOOLEAN, not null, default false)
- target_url (VARCHAR 1024, nullable)
- weight (INTEGER, not null, default 0)
- override_start (TIMESTAMP, nullable)
- override_end (TIMESTAMP, nullable)
- video_id (VARCHAR 255, nullable)
- video_title (VARCHAR 255, nullable)
- channel_id (VARCHAR 255, nullable)
- channel_title (VARCHAR 255, nullable)
- duration_seconds (INTEGER, nullable)
- youtube_published_at (TIMESTAMP, nullable)
- thumbnail_url (VARCHAR 512, nullable)

Indexes:
- idx_advertisement_title (title)
- idx_advertisement_source (source)
- idx_advertisement_active (active)

### advertisement_geo_targets
- advertisement_id (BIGINT, FK -> advertisements.id, not null)
- country_code (VARCHAR 10, not null)
- region (VARCHAR 255, nullable)
- city (VARCHAR 255, nullable)
- latitude (DOUBLE, nullable)
- longitude (DOUBLE, nullable)
- radius_km (INTEGER, nullable)
- include (BOOLEAN, not null)

Indexes:
- idx_geo_target_country (country_code)

### advertisement_bio_targets
- advertisement_id (BIGINT, FK -> advertisements.id, not null)
- min_age (INTEGER, nullable)
- max_age (INTEGER, nullable)
- gender (VARCHAR 20, nullable)
- occupation (VARCHAR 255, nullable)
- education_level (VARCHAR 255, nullable)
- language (VARCHAR 50, nullable)
- interest_category (VARCHAR 255, nullable)
- include (BOOLEAN, not null)

### advertisement_mood_targets
- advertisement_id (BIGINT, FK -> advertisements.id, not null)
- mood (VARCHAR 20, not null)
- intensity_min (INTEGER, nullable)
- intensity_max (INTEGER, nullable)
- time_of_day (VARCHAR 50, nullable)
- day_of_week (VARCHAR 50, nullable)
- season (VARCHAR 50, nullable)
- include (BOOLEAN, not null)

Indexes:
- idx_mood_target_mood (mood)

## Conventions
- Timestamps are stored as TIMESTAMP (timezone handling defined at application level)
- Foreign key constraints reference the primary key of `advertisements`
- Indexes created for common query filters

## Migration Strategy
- All schema changes are applied via Liquibase change sets
- Each change set should be immutable and uniquely identified
- New files should be included in `db.changelog-master.xml`
