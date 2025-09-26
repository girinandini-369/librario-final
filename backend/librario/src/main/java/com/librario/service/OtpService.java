package com.librario.service;

import com.librario.entity.OtpCode;
import com.librario.repository.OtpRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    @Value("${otp.expiration.minutes:5}")
    private int otpExpirationMinutes;

    @Transactional
    public String generateAndSendOtp(String email) {
        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        LocalDateTime now = LocalDateTime.now();

        // Remove old OTP
        otpRepository.deleteByEmail(email);

        // Save new OTP
        OtpCode otpCode = new OtpCode();
        otpCode.setEmail(email);
        otpCode.setOtp(otp);
        otpCode.setCreatedAt(now);
        otpCode.setExpiresAt(now.plusMinutes(otpExpirationMinutes));
        otpRepository.save(otpCode);

        // Send email
        sendOtpEmail(email, otp);
        return otp;
    }

    private void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Your OTP for Librario Password Reset");
            helper.setText("Your OTP is: " + otp + "\n\nThis OTP will expire in "
                    + otpExpirationMinutes + " minutes.");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Transactional
    public boolean validateOtp(String email, String otp) {
        return otpRepository.findByEmailAndOtp(email, otp)
                .map(otpCode -> {
                    if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
                        otpRepository.delete(otpCode);
                        return false;
                    }
                    otpRepository.delete(otpCode); // single use
                    return true;
                })
                .orElse(false);
    }
}
