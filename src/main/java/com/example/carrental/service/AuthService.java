package com.example.carrental.service;

import com.example.carrental.dto.AuthRequestDto;
import com.example.carrental.dto.AuthResponseDto;
import com.example.carrental.dto.RegisterRequestDto;
import com.example.carrental.entity.RefreshToken;
import com.example.carrental.entity.Role;
import com.example.carrental.entity.User;
import com.example.carrental.enums.RoleName;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.repository.RoleRepository;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.security.CustomUserDetails;
import com.example.carrental.security.JwtService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthResult register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        AuthResponseDto response = new AuthResponseDto(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole().getName().name(),
                accessToken
        );

        return new AuthResult(response, refreshToken.getToken());
    }

    public AuthResult login(AuthRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponseDto response = new AuthResponseDto(
                "Login successful",
                userDetails.getId(),
                userDetails.getUsername(),
                user.getRole().getName().name(),
                accessToken
        );

        return new AuthResult(response, refreshToken.getToken());
    }

    public AuthResult refresh(String refreshTokenValue) {

        RefreshToken oldToken = refreshTokenService.verifyToken(refreshTokenValue);

        RefreshToken newToken = refreshTokenService.rotateToken(oldToken);

        User user = newToken.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateToken(userDetails);

        AuthResponseDto response = new AuthResponseDto(
                "Token refreshed",
                user.getId(),
                user.getUsername(),
                user.getRole().getName().name(),
                accessToken
        );

        return new AuthResult(response, newToken.getToken());
    }

    public void logout(String refreshTokenValue) {

        RefreshToken token = refreshTokenService.verifyToken(refreshTokenValue);

        refreshTokenService.revokeToken(token);
    }

    @Getter
    @RequiredArgsConstructor
    public static class AuthResult {
        private final AuthResponseDto response;
        private final String refreshToken;
    }
}