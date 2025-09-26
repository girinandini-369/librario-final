package com.librario.controller;

import com.librario.dto.ResetPasswordRequest;
import com.librario.dto.OtpRequest;
import com.librario.service.OtpService;
import com.librario.service.UserService;
import com.librario.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final UserService userService;
    private final EmailService emailService;

    // Step 1: Forgot password (generate + send OTP)
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody OtpRequest request) {
        boolean userExists = userService.existsByEmail(request.getEmail());
        if (!userExists) {
            return ResponseEntity.badRequest().body("❌ Email not registered");
        }
        String otp = otpService.generateAndSendOtp(request.getEmail());

        // 📩 Email to member
        emailService.sendEmail(
                request.getEmail(),
                "🔑 Your OTP",
                "Your OTP is: " + otp
        );

        // 📩 Email to admin
        emailService.sendEmailToAdmin(
                "🔑 OTP Requested",
                "OTP requested for email: " + request.getEmail() + "\nOTP: " + otp
        );

        return ResponseEntity.ok("✅ OTP sent successfully");
    }

    // Step 2: Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean valid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!valid) {
            return ResponseEntity.badRequest().body("❌ Invalid or expired OTP");
        }
        userService.updatePassword(request.getEmail(), request.getNewPassword());

        // 📩 Email to member
        emailService.sendEmail(
                request.getEmail(),
                "🔒 Password Reset Successful",
                "Hello,\n\nYour password has been successfully reset."
        );

        // 📩 Email to admin
        emailService.sendEmailToAdmin(
                "🔒 Password Reset",
                "Password reset for user: " + request.getEmail()
        );

        return ResponseEntity.ok("✅ Password reset successfully");
    }
}
