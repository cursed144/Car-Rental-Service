package com.example.carrental.service;

import com.example.carrental.dto.UserRequestDto;
import com.example.carrental.dto.UserResponseDto;
import com.example.carrental.entity.Role;
import com.example.carrental.entity.User;
import com.example.carrental.enums.RoleName;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.ResourceNotFoundException;
import com.example.carrental.repository.RoleRepository;
import com.example.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private UserResponseDto mapToResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().getName().name()
        );
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToResponseDto(user);
    }

    public UserResponseDto createUser(UserRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        RoleName roleName;

        try {
            roleName = RoleName.valueOf(requestDto.getRoleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return mapToResponseDto(savedUser);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userRepository.existsByEmailAndIdNot(requestDto.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsernameAndIdNot(requestDto.getUsername(), id)) {
            throw new BadRequestException("Username already exists");
        }

        RoleName roleName;

        try {
            roleName = RoleName.valueOf(requestDto.getRoleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setEmail(requestDto.getEmail());
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(role);

        User updatedUser = userRepository.save(user);

        return mapToResponseDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.deleteById(id);
    }
}