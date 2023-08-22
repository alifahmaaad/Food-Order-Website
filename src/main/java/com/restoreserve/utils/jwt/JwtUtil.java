package com.restoreserve.utils.jwt;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
    private String SECRET_KEY = "your-secret-key"; // Ganti dengan kunci rahasia Anda
    private long EXPIRATION_TIME = 86400000; // 24 jam
    public String generateToken(UserDetails userDetails){
        Set<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        return createToken(userDetails.getUsername(),roles);
    }
    private String createToken(String subject ,Set<String> role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String extractUsername(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        Date expirationDate= claims.getExpiration();
        return expirationDate.before(new Date());
    }
}
