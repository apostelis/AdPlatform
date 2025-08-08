package com.example.adplatform.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    @Test
    void shouldReturn429AfterExceedingLimitWithinWindow() throws ServletException, IOException {
        // limit = 2 requests per very long window to make test deterministic
        RateLimitingFilter filter = new RateLimitingFilter(2, 60_000);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response1 = mock(HttpServletResponse.class);
        HttpServletResponse response2 = mock(HttpServletResponse.class);
        HttpServletResponse response3 = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/advertisements");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // First request should pass
        filter.doFilterInternal(request, response1, chain);
        verify(chain, times(1)).doFilter(request, response1);

        // Second request should pass
        filter.doFilterInternal(request, response2, chain);
        verify(chain, times(1)).doFilter(request, response2);

        // Third request should be rate limited
        filter.doFilterInternal(request, response3, chain);
        verify(response3).setStatus(429);
        verify(chain, times(0)).doFilter(request, response3);
    }
}
