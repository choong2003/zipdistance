package com.sc.zipdistance.util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.sc.zipdistance.model.entity.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static long EXPIRATION; // Static variable
    @Autowired
    private RsaEncryptionUtil rsaEncryptionUtil;
    private long expirationTime; // Injected by Spring

    public static long getExpiration() {
        return EXPIRATION;
    }

    public String generateToken(UserDetails userDetails) throws Exception {
        PrivateKey privateKey = rsaEncryptionUtil.getPrivateKey();
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        // Separate roles and permissions
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_")) // Identify roles
                .collect(Collectors.toList());

        List<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_")) // Identify permissions
                .collect(Collectors.toList());

        // Build JWT with separate claims
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", ((CustomUserDetails) userDetails).getUserId())
                .claim("roles", roles)          // Add roles as a list
                .claim("permissions", permissions)  // Add permissions as a list
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + getExpiration())) // e.g., 10h token
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }


    public Claims validateToken(String token) throws Exception {
        PublicKey publicKey = rsaEncryptionUtil.getPublicKey();

        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @PostConstruct
    public void init() {
        EXPIRATION = expirationTime; // Assign value after Spring injection
    }

    @Value("${jwt.expiration}")
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
