package com.example.adplatform.infrastructure.web.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Simple in-memory rate limiting filter.
 *
 * - Scope: API endpoints only (paths under /api/)
 * - Key: Client IP address (uses X-Forwarded-For if present)
 * - Limit: configurable requests per fixed window
 * - Storage: Caffeine cache
 *
 * Note: This is a lightweight safeguard intended for a single-node setup
 * or as a baseline. For distributed environments, consider a centralized
 * rate limiter (e.g., Redis, API Gateway) per Architecture tasks.
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final class RequestCounter {
        final long windowStartMs;
        int count;
        RequestCounter(long windowStartMs, int count) {
            this.windowStartMs = windowStartMs;
            this.count = count;
        }
    }

    private final Cache<String, RequestCounter> cache;
    private final int limit;
    private final long windowMs;

    public RateLimitingFilter() {
        this(100, TimeUnit.MINUTES.toMillis(1));
    }

    public RateLimitingFilter(int limit, long windowMs) {
        this.limit = limit;
        this.windowMs = windowMs;
        // Keep entries a bit longer than the window to avoid churn
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = resolveClientIp(request);

        long now = Instant.now().toEpochMilli();
        long currentWindowStart = (now / windowMs) * windowMs;

        RequestCounter counter = cache.get(clientIp, k -> new RequestCounter(currentWindowStart, 0));

        synchronized (counter) {
            if (counter.windowStartMs != currentWindowStart) {
                // New window
                counter.count = 0;
            }
            counter.count++;
            if (counter.count > limit) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                // Avoid writing body here to be safe with mocked responses in tests and to keep filter lightweight
                // Clients can rely on status code; optional headers for backoff can be added later
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xfwd = request.getHeader("X-Forwarded-For");
        if (xfwd != null && !xfwd.isBlank()) {
            // first IP in the list
            int comma = xfwd.indexOf(',');
            return comma > 0 ? xfwd.substring(0, comma).trim() : xfwd.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return Objects.toString(request.getRemoteAddr(), "unknown");
    }
}
