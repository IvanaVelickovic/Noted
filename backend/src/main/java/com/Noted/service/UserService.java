package com.Noted.service;

import com.Noted.config.SecurityConfig;
import com.Noted.dto.LoginRequest;
import com.Noted.dto.RegisterRequest;
import com.Noted.exception.EmailAlreadyExistsException;
import com.Noted.model.User;
import com.Noted.repository.UserRepository;
import com.Noted.response.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JWTService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User createUser(RegisterRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email is already in use: " + request.email());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    public LoginResponse authenticateUser(LoginRequest request){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException ex){
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        return jwtService.issueTokensFor(user);
    }

    public String refreshAccessToken(String refreshToken){
        return jwtService.refreshAccessToken(refreshToken);
    }

    public void logout(String refreshToken){
        jwtService.revokeRefreshToken(refreshToken);
    }
}
