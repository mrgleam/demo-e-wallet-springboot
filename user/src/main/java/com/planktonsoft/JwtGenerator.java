package com.planktonsoft;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final String DEFAULT_ROLE = "usr";
    private static final String ISSUER_NAME = "unclequin.me";
    private static final long ONE_HOUR_EXPIRATION = 3600000; // 1 hour in milliseconds

    public String generateUserToken(String phoneNumber) {
        Map<String, Object> claims = createClaims(phoneNumber);
        return generateJwtToken(claims, jwtSecret);
    }

    private Map<String, Object> createClaims(String phoneNumber) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", phoneNumber);
        claims.put("role", DEFAULT_ROLE);
        claims.put("exp", new Date(System.currentTimeMillis() + ONE_HOUR_EXPIRATION));
        return claims;
    }

    private String generateJwtToken(Map<String, Object> claims, String secret) {
        Key key = createSigningKey(secret);

        return Jwts.builder()
                .issuedAt(new Date())
                .issuer(ISSUER_NAME)
                .claims(claims)
                .signWith(key)
                .compact();
    }

    private Key createSigningKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }

}
