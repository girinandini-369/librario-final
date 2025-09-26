package com.librario.controller;

import com.librario.dto.AuthRequest;
import com.librario.dto.AuthResponse;
import com.librario.dto.MessageResponse;
import com.librario.entity.Member;
import com.librario.entity.Role;
import com.librario.entity.User;
import com.librario.repository.MemberRepository;
import com.librario.repository.RoleRepository;
import com.librario.repository.UserRepository;
import com.librario.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@RequestBody AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("❌ Email already exists"));
        }

        Role memberRole = roleRepository.findByRoleName("ROLE_MEMBER")
                .orElseThrow(() -> new RuntimeException("❌ Default role not found"));

        User newUser = User.builder()
                .email(request.getEmail().trim())
                .name(request.getName().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(memberRole)
                .build();
        userRepository.save(newUser);

        Member member = new Member();
        member.setUser(newUser);
        member.setStatus("ACTIVE");
        memberRepository.save(member);

        // 📩 Email to user
        emailService.sendEmail(
                newUser.getEmail(),
                "🎉 Welcome to Librario",
                "Hello " + newUser.getName() + ",\n\nYour account has been created.\nEmail: "
                        + newUser.getEmail() + "\nPassword: " + request.getPassword()
        );

        // 📩 Email to admin
        emailService.sendEmailToAdmin(
                "🎉 New Member Registered",
                "New user registered:\n\nEmail: " + newUser.getEmail() + "\nName: " + newUser.getName()
        );

        return ResponseEntity.ok(new MessageResponse("✅ Member registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("❌ User not found"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("❌ Invalid password"));
        }

        Long memberId = null;
        if ("ROLE_MEMBER".equals(user.getRole().getRoleName())) {
            memberId = memberRepository.findByUserId(user.getId())
                    .map(Member::getId)
                    .orElse(null);
        }

        return ResponseEntity.ok(new AuthResponse(
                "✅ Login successful",
                user.getRole().getRoleName().replace("ROLE_", ""),
                memberId
        ));
    }
}
