package com.yourcompany.streamapp.user_service.config;


import com.yourcompany.streamapp.user_service.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Make this a Spring-managed bean
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Check if the request has a valid JWT in the "Authorization" header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // If not, pass the request to the next filter
            return;
        }

        // 2. Extract the token from the header
        jwt = authHeader.substring(7); // "Bearer ".length() is 7

        // 3. Extract the username from the token
        username = jwtUtils.extractUsername(jwt);

        // 4. If we have a username AND the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. Load the user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Check if the token is valid for this user
            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                // 7. If valid, create an authentication token and set it in the Security Context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are not needed as we are using JWT
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // This is the line that authenticates the user for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 8. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
