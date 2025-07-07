package com.yourcompany.streamapp.api_gateway.config;

import com.yourcompany.streamapp.api_gateway.jwt.JwtUtils; // Make sure package name is correct
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtils jwtUtils;

    public AuthenticationFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Allow public access to auth endpoints
            if (request.getURI().getPath().contains("/api/auth")) {
                return chain.filter(exchange);
            }

            // Check for Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            } else {
                return onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Validate JWT and extract username
                jwtUtils.validateToken(jwt);
                String username = jwtUtils.extractUsername(jwt);

                // Add username to request headers for downstream services
                ServerHttpRequest newRequest = request.mutate()
                        .header("X-Authenticated-Username", username)
                        // Remove the original auth header to prevent confusion downstream
                        .headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION))
                        .build();

                return chain.filter(exchange.mutate().request(newRequest).build());
            } catch (Exception e) {
                return onError(exchange, "Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        // You could optionally write the error message to the response body here
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Empty config class, needed by the abstract factory
    }
}