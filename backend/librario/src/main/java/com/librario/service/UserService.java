package com.librario.service;

import com.librario.dto.UserDto;
import com.librario.entity.Role;
import com.librario.entity.User;
import com.librario.repository.RoleRepository;
import com.librario.repository.UserRepository;
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

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .roleName(user.getRole() != null ? user.getRole().getRoleName().replace("ROLE_", "") : null)
                .build();
    }

    private User fromDto(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        user.setPassword(passwordEncoder.encode("default123"));

        if (dto.getRoleName() != null) {
            String rn = dto.getRoleName().toUpperCase();
            Role role = roleRepository.findByRoleNameIgnoreCase("ROLE_" + rn)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + rn));
            user.setRole(role);
        }

        return user;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto createUser(UserDto dto) {
        User user = fromDto(dto);
        return toDto(userRepository.save(user));
    }

    public UserDto updateUser(UserDto dto) {
        return userRepository.findById(dto.getId())
                .map(existing -> {
                    if (dto.getName() != null) existing.setName(dto.getName());
                    if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
                    if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

                    if (dto.getRoleName() != null) {
                        String rn = dto.getRoleName().toUpperCase();
                        Role role = roleRepository.findByRoleNameIgnoreCase("ROLE_" + rn)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + rn));
                        existing.setRole(role);
                    }

                    return toDto(userRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + dto.getId()));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    // ✅ NEW METHODS for OTP flow
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
