package com.ayudaz.ayudaz_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key key;

    @PostConstruct
    public void init() {

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Extraer username
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    // Extraer expiración
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer claim genérico
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    // Obtener todos los claims
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verificar expiración
    private Boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    // Generar token simple
    public String generateToken(String username) {

        Map<String, Object> claims =
                new HashMap<>();

        return createToken(claims, username);
    }

    // Generar token con claims extra
    public String generateToken(
            String username,
            Map<String, Object> extraClaims
    ) {

        return createToken(extraClaims, username);
    }

    // Crear token
    private String createToken(
            Map<String, Object> claims,
            String subject
    ) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(
                        new Date(System.currentTimeMillis())
                )
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(
                        key,
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    // Validar token con username
    public Boolean validateToken(
            String token,
            String username
    ) {

        final String extractedUsername =
                extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    // Validar token general
    public Boolean isTokenValid(String token) {

        try {

            extractAllClaims(token);

            return !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }
}