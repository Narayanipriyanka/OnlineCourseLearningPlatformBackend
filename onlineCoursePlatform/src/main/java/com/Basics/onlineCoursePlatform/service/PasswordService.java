package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.entity.PasswordResetToken;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.repository.PasswordResetTokenRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void sendResetToken(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(user, token, LocalDateTime.now().plusHours(1), false);

        tokenRepo.save(resetToken);

        // TODO: Implement actual email sending logic
        System.out.println("Reset token sent to email: " + token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByTokenAndIsUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or used token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetToken.setIsUsed(true);
        tokenRepo.save(resetToken);
    }
}
