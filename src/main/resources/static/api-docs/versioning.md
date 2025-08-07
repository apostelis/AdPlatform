# API Versioning Strategy

## Overview

This document describes the API versioning strategy used in the Advertisement Platform. API versioning is essential for maintaining backward compatibility while allowing the API to evolve over time.

## Versioning Approaches

The Advertisement Platform uses two complementary versioning approaches:

### 1. URI Path Versioning (Primary)

The primary versioning strategy is URI path versioning, where the version is included in the URL path.

**Example:**
```
/api/v1/advertisements
```

This approach is:
- Simple and explicit
- Easy to understand and use
- Visible in browser address bars and server logs
- Compatible with caching mechanisms

### 2. Content Negotiation (Secondary)

As a secondary approach, the API also supports content negotiation through the `Accept` header.

**Example:**
```
Accept: application/vnd.adplatform.v1+json
```

This approach:
- Keeps the URI clean
- Follows REST principles more closely
- Allows for more flexibility in versioning

## Current API Versions

| Version | Status | Release Date | End of Life |
|---------|--------|--------------|-------------|
| v1      | Active | 2025-08-07   | TBD         |
| Legacy  | Deprecated | Before 2025-08-07 | TBD |

## Backward Compatibility

The Advertisement Platform maintains backward compatibility through:

1. **Deprecated Controllers**: Legacy endpoints are marked with `@Deprecated` but continue to function.
2. **Version-specific Controllers**: Each API version has its own controller implementation.
3. **Documentation**: Clear documentation of changes between versions.

## Migration Guide

### Moving from Legacy to v1

To migrate from the legacy API to v1:

1. Update your API endpoint URLs to include the `/api/v1/` prefix.
2. Alternatively, add the `Accept: application/vnd.adplatform.v1+json` header to your requests.
3. Test your integration thoroughly after migration.

## Best Practices for API Consumers

1. Always specify the API version explicitly in your requests.
2. Monitor API documentation for announcements about new versions and deprecations.
3. Plan to migrate to newer API versions before the end-of-life date of your current version.
4. Test your integration thoroughly when migrating to a new API version.

## Versioning Policy

1. **Breaking Changes**: Any breaking changes will trigger a new API version.
2. **Non-breaking Changes**: Non-breaking changes (additions) may be made to existing API versions.
3. **Deprecation Notice**: API versions will be deprecated with advance notice before being removed.
4. **Support Period**: Deprecated API versions will be supported for a minimum of 6 months after deprecation.