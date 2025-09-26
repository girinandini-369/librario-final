package com.librario.controller;

import com.librario.dto.UserDto;
import com.librario.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarian")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class LibrarianController {

    private final UserService userService;

    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Create new user
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    // Update user
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
