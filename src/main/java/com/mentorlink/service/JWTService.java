package com.mentorlink.service;

import com.mentorlink.constant.RoleConstant;
import com.mentorlink.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWTService {


    private String SECRET_KEY = "";
    private static final int JWT_EXPIRATION_IN_MINUTES = 30;


    public JWTService() {

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            SECRET_KEY = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Extract role names
                .collect(Collectors.toList());
        claims.put(RoleConstant.SCOPE, roles);
        claims.put("userId", user.getId());
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (JWT_EXPIRATION_IN_MINUTES * 60 * 1000)))
                .and()
                .signWith(getKey())
                .compact();

    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String getExpiration(String token){
        return extractExpiration(token).toString();
    }

    public String getSubject(String token){
        return  extractUserName(token);
    }

    public Set<String> getScopes(String token) {
        return extractClaim(token, claims -> {
            // Extract the roles claim directly as a List of Strings
            List<String> rolesList = claims.get(RoleConstant.SCOPE, List.class);
            // Convert the List to a Set to ensure uniqueness
            return new HashSet<>(rolesList);
        });
    }

}
