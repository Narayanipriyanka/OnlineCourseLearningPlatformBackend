package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.entity.PasswordResetToken;
import com.Basics.onlineCoursePlatform.entity.Session;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.model.LoginRequest;
import com.Basics.onlineCoursePlatform.model.RegisterRequest;
import com.Basics.onlineCoursePlatform.model.Role;
import com.Basics.onlineCoursePlatform.repository.PasswordResetTokenRepository;
import com.Basics.onlineCoursePlatform.repository.SessionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import com.Basics.onlineCoursePlatform.service.EmailService;
import com.Basics.onlineCoursePlatform.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private SessionRepository sessionRepository;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user=new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());

        user.setPassword(encoder.encode(req.getPassword()));
        user.setAvatar(req.getAvatar());
        user.setBio(req.getBio());
        user.setRole(req.getRole());
        return ResponseEntity.ok(userRepository.save(user));
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        if (auth.isAuthenticated())
            return jwtService.generateToken(req.getEmail());;

        return "fail";
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email).orElseThrow();
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, LocalDateTime.now().plusHours(1), false);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendEmail(user.getEmail(), "Password Reset Token", "Your password reset token is: " + token);
        return ResponseEntity.ok("Password reset token sent to your email");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElseThrow();
        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now()) || passwordResetToken.getIsUsed()) {
            return ResponseEntity.badRequest().body("Reset token has expired or is already used");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        passwordResetToken.setIsUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
        return ResponseEntity.ok("Password reset successfully");
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request, Principal principal) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Old password and new password are required");
        }

        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " from the token
        Optional<Session> sessionOptional = sessionRepository.findByToken(jwtToken);
        if (sessionOptional.isPresent()) {
            sessionRepository.delete(sessionOptional.get());
        }
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateProfile(@RequestPart(value = "name", required = false) String name,
                                                @RequestPart(value = "bio", required = false) String bio,
                                                @RequestPart(value = "avatar", required = false) MultipartFile avatar,
                                                Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (name != null) {
            user.setName(name);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (avatar != null) {
            // Save the avatar file
            String avatarUrl = saveAvatar(avatar);
            user.setAvatar(avatarUrl);
        }
        userRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully");
    }

    private String saveAvatar(MultipartFile avatar) {
        try {
            // Create the upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename for the avatar
            String filename = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();

            // Save the avatar file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(avatar.getInputStream(), filePath);

            // Return the URL of the saved avatar file
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar file", e);
        }
    }
}
