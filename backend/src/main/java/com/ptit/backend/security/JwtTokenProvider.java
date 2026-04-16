package com.ptit.backend.security;

import com.ptit.backend.entity.Role;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMs;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.expiration-ms:86400000}") long expirationMs,
            @Value("${security.jwt.issuer:bookstore-backend}") String issuer
    ) {
        this.signingKey = buildSigningKey(jwtSecret);
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return generateToken(userDetails.getUsername(), roles);
    }

    public String generateToken(User user) {
        String roleName = normalizeRoleName(user.getRole());
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .claim("user_id", user.getUserId())
                .claim("username", user.getUsername())
                .claim("full_name", user.getFullName())
                .claim("email", user.getEmail())
                .claim("primary_role", roleName)
                .claim("roles", List.of("ROLE_" + roleName))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateToken(String username, Collection<String> roles) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .claim("roles", roles)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = extractAllClaims(token).get("roles");
        if (roles instanceof List<?> roleList) {
            return (List<String>) roleList;
        }
        return List.of();
    }

    public boolean validateToken(String token) {
        extractAllClaims(token);
        return true;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private String normalizeRoleName(Role role) {
        String roleName = role != null ? role.getRoleName() : null;
        if (!StringUtils.hasText(roleName)) {
            return "USER";
        }
        String trimmed = roleName.trim().toUpperCase();
        return trimmed.startsWith("ROLE_") ? trimmed.substring(5) : trimmed;
    }

    private Claims extractAllClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Token khong duoc de trong");
        }

        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException exception) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    private SecretKey buildSigningKey(String jwtSecret) {
        if (!StringUtils.hasText(jwtSecret)) {
            throw new IllegalStateException("Gia tri security.jwt.secret chua duoc cau hinh");
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException exception) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("security.jwt.secret phai co do dai toi thieu 32 bytes");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
