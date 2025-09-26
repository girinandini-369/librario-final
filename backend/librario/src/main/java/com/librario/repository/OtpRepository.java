package com.librario.repository;

import com.librario.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {

    void deleteByEmail(String email);

    Optional<OtpCode> findByEmailAndOtp(String email, String otp);
}
