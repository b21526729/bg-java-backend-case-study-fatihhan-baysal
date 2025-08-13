package com.example.bilisimgarajitask.common.Jwt;

import com.example.bilisimgarajitask.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")           // ms
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration}")   // ms
    private long refreshExpirationMs;

    private Key key() {
        // secret >= 32 chars olmalı (HS256 için 256-bit)
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setId(UUID.randomUUID().toString())
                .claim("uid", user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("pid", user.getProfileId())
                .claim("typ", "access")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessExpirationMs)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user){
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setId(UUID.randomUUID().toString())
                .claim("uid", user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("pid", user.getProfileId())
                .claim("typ", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(refreshExpirationMs)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }

    public String getUsername(String token){
        return parse(token).getBody().getSubject();
    }

    public String getType(String token){
        Object typ = parse(token).getBody().get("typ");
        return typ == null ? "access" : typ.toString();
    }
}
