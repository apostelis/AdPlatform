# Targeting Algorithms and Business Rules

This document describes how advertisement targeting decisions are made in the platform.

## Overview
Targeting combines multiple dimensions to determine if an advertisement is eligible for a given request:
- Geo Targeting (country, region, city, geofence radius)
- Bio/Demographic Targeting (age, gender, occupation, education, language, interests)
- Mood/Context Targeting (mood with optional intensity, time of day, day of week, season)

Each dimension can include or exclude criteria. An advertisement is eligible when it satisfies all applicable include rules and does not match any exclude rules across dimensions.

## Evaluation Order
1. Exclusion rules checked first; any match disqualifies the ad early.
2. Inclusion rules: all specified include rules must be satisfied.
3. If a dimension has no rules specified, it is treated as neutral (does not affect eligibility).

## Geo Targeting
- Country: ISO codes compared case-insensitively.
- Region/City: string match; if provided, must match input values.
- Radius targeting: if latitude/longitude with radius_km is set and request has coordinates, Haversine distance must be within radius_km.

## Bio/Demographic Targeting
- Age range: request age must be between min_age and max_age when defined.
- Gender: match on provided gender when defined.
- Language: prefer exact match; if multiple languages provided, any match suffices.
- Interests: category string match; multiple categories use OR semantics within dimension.

## Mood/Context Targeting
- Mood: exact match on mood label.
- Intensity: if min/max defined, the provided intensity must be within bounds.
- Time/Day/Season: if provided, must equal request context values.

## Performance Considerations
- Prefer pre-filtering in the database with indexes on common fields (title, source, active, country_code, mood).
- Cache frequently accessed ads (already implemented) and warm caches for popular segments.

## Extensibility
- Future work: refactor selection logic into Strategy pattern (see task) to add new dimensions without modifying core logic.

## Edge Cases
- Missing context fields: treat as neutral unless an include rule explicitly requires the field.
- Conflicting rules within the same dimension: exclusion takes precedence over inclusion.
- Invalid input: validation layer should reject requests with inconsistent values (e.g., negative age, invalid coordinates).
