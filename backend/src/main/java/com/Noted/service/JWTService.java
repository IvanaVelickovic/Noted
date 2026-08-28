package com.Noted.service;

import com.Noted.model.RefreshToken;
import com.Noted.model.User;
import com.Noted.repository.RefreshTokenRepository;
import com.Noted.response.LoginResponse;
import com.Noted.response.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private final RefreshTokenRepository refreshTokenRepository;

    public JWTService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "access")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) //15 minutes
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user){
        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("type", "refresh")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) //7 days
                .signWith(getSigningKey())
                .compact();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public LoginResponse issueTokensFor(User user){
        return new LoginResponse(generateAccessToken(user), generateRefreshToken(user), UserResponse.fromEntity(user));
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        final String type = extractTokenType(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)
                && "access".equals(type));
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public String refreshAccessToken(String refreshToken){
        String type = extractTokenType(refreshToken);
        if(!"refresh".equals(type)){
            throw new BadCredentialsException("Invalid token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if(storedToken.isRevoked()){
            throw new BadCredentialsException("Refresh token has been revoked");
        }

        if(storedToken.getExpiresAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh token has expired");
        }

        return generateAccessToken(storedToken.getUser());
    }

    public void revokeRefreshToken(String refreshToken){
        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));

        savedRefreshToken.setRevoked(true);
        refreshTokenRepository.save(savedRefreshToken);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

}
