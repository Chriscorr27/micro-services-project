package com.tech.microservices.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

public class JWTUtil {
    private static final String SECRET = "78AaiBmqIz45bsXngI6PznPdJgYov78Y";

    public static String generateToken(String username, Long userId){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuer("microservices")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5))//5 days
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String extractEmail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    public static Long extractUserId(String token) {
        String userId = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        if (userId != null) {
            return Long.parseLong(userId);
        }
        return null;
    }

    public boolean validateToken(String token, String email){
        return extractEmail(token).equals(email);
    }
}
