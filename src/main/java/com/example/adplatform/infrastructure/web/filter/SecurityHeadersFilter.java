package com.example.adplatform.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Adds a baseline set of security headers to every HTTP response.
 * Note: Fine-tune CSP and other headers per environment if needed.
 */
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        // Clickjacking protection
        response.setHeader("X-Frame-Options", "DENY");
        // Basic XSS protection (legacy header; CSP is primary defense)
        response.setHeader("X-XSS-Protection", "1; mode=block");
        // Referrer policy
        response.setHeader("Referrer-Policy", "no-referrer");
        // Content Security Policy (conservative default; adapt as needed)
        // Allow self resources; block inline by default
        if (!response.containsHeader("Content-Security-Policy")) {
            response.setHeader("Content-Security-Policy", "default-src 'self'; object-src 'none'; frame-ancestors 'none'; base-uri 'self'");
        }
        // Permissions Policy (formerly Feature-Policy)
        if (!response.containsHeader("Permissions-Policy")) {
            response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        }

        filterChain.doFilter(request, response);
    }
}
