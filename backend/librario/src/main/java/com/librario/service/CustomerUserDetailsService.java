package com.librario.service;

import com.librario.entity.User;
import com.librario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Ensure the role name is in the correct format: ROLE_MEMBER, ROLE_ADMIN, etc.
        String roleName = user.getRole() != null ? user.getRole().getRoleName().replace("ROLE_", "") : "MEMBER";

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roleName)
                .build();
    }
}
