package com.yourcompany.streamapp.api_gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public void validateToken(String token) {
        Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
