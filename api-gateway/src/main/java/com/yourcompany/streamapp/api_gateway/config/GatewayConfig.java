package com.yourcompany.streamapp.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
//                .route("user-service", r -> r
//                        .path("/api/auth/**", "/api/users/**", "/api/test/**")
//                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
//                        // Use the Docker service name and port directly
//                        .uri("http://user-service:8081"))
                .route("user-service", r -> r
                        .path("/api/auth/**", "/api/users/**", "/api/test/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        // Use the Docker service name and port directly
                        .uri("http://localhost:8081"))
                .route("stream-service", r -> r
                        .path("/api/streams/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        // Use the Docker service name and port directly
//                        .uri("http://stream-service:8082"))
                        .uri("http://localhost:8082"))
                .route("recordingservice", r -> r
                        .path("/api/recordings/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        // Use the Docker service name and port directly
//                        .uri("http://stream-service:8082"))
                        .uri("http://localhost:8084"))
                .route("stream-service-ws", r -> r
                        .path("/ws/signal/**")
                        // NO FILTER APPLIED HERE
                        .uri("ws://localhost:8082")) // Note the ws:// scheme
                // The dedicated, secure route for getting a LiveKit token
//                .route("livekit-token-secure-route", r -> r.path("/api/livekit/token")
//                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
//                        .uri("http://localhost:8082"))
                .build();
    }
}