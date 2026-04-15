package com.hcmut.irms.auth.config;

import com.hcmut.irms.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Look for the "Authorization" header
        final String authHeader = request.getHeader("Authorization");

        // 2. If it's missing or doesn't start with "Bearer ", ignore it and move on
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (Remove the "Bearer " prefix)
        final String jwt = authHeader.substring(7);

        try {
            // 4. If the token is valid AND the user isn't already logged in to this request thread
            if (jwtService.isTokenValid(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtService.extractUsername(jwt);
                String role = jwtService.extractRole(jwt);

                // Spring Security Convention: Roles must start with "ROLE_" (e.g., "ROLE_MANAGER")
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // 5. Create the official Spring Security authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null, // We don't put the password here
                        Collections.singletonList(authority)
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Save the user into the Security Context! They are now officially logged in.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // If the token is invalid, we do nothing. The request will remain unauthenticated
            // and Spring will automatically block it with a 403 Forbidden.
        }

        // 7. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}