package com.example.carrental.service;

import com.example.carrental.config.CustomUserDetails;
import com.example.carrental.dto.AuthRequestDto;
import com.example.carrental.dto.AuthResponseDto;
import com.example.carrental.dto.RegisterRequestDto;
import com.example.carrental.entity.Role;
import com.example.carrental.entity.User;
import com.example.carrental.enums.RoleName;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.repository.RoleRepository;
import com.example.carrental.repository.UserRepository;
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

    public AuthResponseDto register(RegisterRequestDto requestDto) {
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

        return new AuthResponseDto(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole().getName().name()
        );
    }

    public AuthResponseDto login(AuthRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return new AuthResponseDto(
                "Login successful",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        );
    }
}