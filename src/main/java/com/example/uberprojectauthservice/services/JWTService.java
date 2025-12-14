package com.example.uberprojectauthservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService implements CommandLineRunner {

    @Value("${jwt.expiry}")
    private int expiry;

    @Value("${jwt.secret}")
    private String SECRET;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }


    private String createToken(Map<String, Object> payload, String email) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiry * 1000L);

        return Jwts.builder()
                .setClaims(payload)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private <T> T extractPayload(String token, Function<Claims, T> resolverFunction) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return resolverFunction.apply(claims);
    }


    private <T> T extractPayload(String token, String payloadKey, Class<T> type) {
        return extractPayload(token, claims -> claims.get(payloadKey, type));
    }

    private Date extractExpiration(String token) {
        return extractPayload(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String extractEmail(String token) {
        return extractPayload(token, Claims::getSubject);
    }

    private String extractPhoneNumber(String token) {
        return extractPayload(token, "phoneNumber", String.class);
    }

    private Boolean validateToken(String token, String email) {
        final String emailFromToken = extractEmail(token);
        return emailFromToken.equals(email) && !isTokenExpired(token);
    }


    @Override
    public void run(String... args) {

        Map<String, Object> mp = new HashMap<>();
        mp.put("email", "a@b.com");
        mp.put("phoneNumber", "4255555555");

        String token = createToken(mp, "shashank");

        System.out.println("Generated Final Token:");
        System.out.println(token);

        String emailFromToken = extractPayload(token, "email", String.class);
        String phoneFromToken = extractPayload(token, "phoneNumber", String.class);

        System.out.println("Extracted Email: " + emailFromToken);
        System.out.println("Extracted Phone Number: " + phoneFromToken);
    }
}
