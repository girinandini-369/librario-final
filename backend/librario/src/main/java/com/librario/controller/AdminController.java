package com.librario.controller;

import com.librario.dto.CreateLibrarianRequest;
import com.librario.dto.MessageResponse;
import com.librario.entity.Role;
import com.librario.entity.User;
import com.librario.repository.RoleRepository;
import com.librario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/librarian")
    public ResponseEntity<?> createLibrarian(@RequestBody CreateLibrarianRequest request) {

        if (userRepository.existsByEmail(request.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("❌ Email already exists"));
        }

        Role librarianRole = roleRepository.findByRoleName("ROLE_LIBRARIAN")
                .orElseThrow(() -> new RuntimeException("❌ Librarian role not found"));

        User librarian = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(librarianRole)
                .build();

        userRepository.save(librarian);
        return ResponseEntity.ok(new MessageResponse("✅ Librarian created successfully"));
    }
}
