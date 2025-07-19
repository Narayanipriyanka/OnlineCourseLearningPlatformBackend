package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    @Query("SELECT p FROM PasswordResetToken p WHERE p.token = :token AND p.isUsed = false")
    Optional<PasswordResetToken> findByTokenAndIsUsedFalse(String token);

}

