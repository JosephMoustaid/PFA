package spring.bricole.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import spring.bricole.common.Role;
import spring.bricole.exceptions.InvalidTokenException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    // Configuration - move these to application.properties in production
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_EXPIRATION = 86400000; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days

    // Generate both access and refresh tokens
    public static Map<String, String> generateTokens(int userId, Role role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", buildToken(userId, role, ACCESS_TOKEN_EXPIRATION));
        tokens.put("refresh_token", buildToken(userId, role, REFRESH_TOKEN_EXPIRATION));
        return tokens;
    }

    private static String buildToken(int userId, Role role, long expiration) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Improved token validation
    public static TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new TokenValidationResult(
                    Integer.parseInt(claims.getSubject()),
                    Role.valueOf(claims.get("role", String.class)),
                    claims.getExpiration()
            );
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token expired");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid token signature");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    // Token validation result container
    public record TokenValidationResult(int userId, Role role, Date expiration) {}
}