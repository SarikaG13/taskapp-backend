package com.example.TaskApp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    private String getTokenFromRequest(HttpServletRequest request) {
        final String tokenWithBearer = request.getHeader("Authorization");
        if (tokenWithBearer == null) {
            log.warn("🚫 Missing Authorization header");
            return null;
        }
        if (!tokenWithBearer.startsWith("Bearer ")) {
            log.warn("⚠️ Authorization header does not start with 'Bearer ': {}", tokenWithBearer);
            return null;
        }
        return tokenWithBearer.substring(7);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("➡️ Incoming request: {} {}", request.getMethod(), path);

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            log.info("🛂 OPTIONS request detected, sending CORS headers");
            response.setHeader("Access-Control-Allow-Origin", "https://taskapp-frontend-8x0n.onrender.com");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (path.startsWith("/api/auth/") || path.equals("/api/tasks/test-email")) {
            log.info("🔓 Public path detected, skipping JWT check: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = getTokenFromRequest(request);

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                log.info("🔐 JWT received: {}", jwt);

                String username = jwtUtils.getUsernameFromToken(jwt);
                log.info("👤 Extracted username from token: {}", username);

                if (username == null || username.isBlank()) {
                    log.warn("❌ Username extracted from token is null or blank");
                    filterChain.doFilter(request, response);
                    return;
                }

                AuthUser authUser = (AuthUser) customUserDetailsService.loadUserByUsername(username);
                log.info("👤 Loaded AuthUser: {}", authUser.getUsername());
                log.info("🔍 AuthUser loaded: username={}, email={}", authUser.getUsername(), authUser.getUser().getEmail());

                boolean valid = jwtUtils.isTokenValid(jwt, authUser);
                log.info("✅ Token valid: {}", valid);

                if (valid) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    log.info("🔐 SecurityContext set for user: {}", authUser.getUsername());
                } else {
                    log.warn("❌ Token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                log.error("💥 Exception during token validation", e);
            }
        } else {
            log.debug("⚠️ No token found or user already authenticated");
        }

        filterChain.doFilter(request, response);
    }
}