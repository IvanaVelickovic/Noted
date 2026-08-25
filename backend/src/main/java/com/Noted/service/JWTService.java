package com.Noted.service;

import com.Noted.model.RefreshToken;
import com.Noted.model.User;
import com.Noted.repository.RefreshTokenRepository;
import com.Noted.response.LoginResponse;
import com.Noted.response.UserResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) //15 minutes
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user){
        String token = Jwts.builder()
                .subject(user.getEmail())
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
}
