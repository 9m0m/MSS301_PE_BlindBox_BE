package com.mss301.msblindbox_se184531.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.role-claim:role}")
    private String roleClaimName;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Integer getRoleFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get(roleClaimName, Integer.class);
    }

    public Integer getIntegerClaim(String token, String claimName) {
        Claims claims = extractClaims(token);
        return claims.get(claimName, Integer.class);
    }

    public String getStringClaim(String token, String claimName) {
        Claims claims = extractClaims(token);
        return claims.get(claimName, String.class);
    }

    public Boolean getBooleanClaim(String token, String claimName) {
        Claims claims = extractClaims(token);
        return claims.get(claimName, Boolean.class);
    }
}
