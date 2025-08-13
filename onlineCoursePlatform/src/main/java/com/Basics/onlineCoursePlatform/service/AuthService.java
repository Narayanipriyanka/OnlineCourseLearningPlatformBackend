package com.Basics.onlineCoursePlatform.service;

import com.Basics.onlineCoursePlatform.DTO.LoginRequest;
import com.Basics.onlineCoursePlatform.DTO.UserDTO;
import com.Basics.onlineCoursePlatform.entity.*;
import com.Basics.onlineCoursePlatform.exception.EmailAlreadyExistsException;

import com.Basics.onlineCoursePlatform.repository.PasswordResetTokenRepository;
import com.Basics.onlineCoursePlatform.repository.SessionRepository;
import com.Basics.onlineCoursePlatform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SessionRepository sessionRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ModelMapper modelMapper;
    public ResponseEntity<User> register( MultipartFile avatarFile,String name,String email,String password,String bio,Role role) {
        try {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new EmailAlreadyExistsException("Email already exists");
            }

            User user = modelMapper.map(new UserDTO(name, email, bio,avatarFile.getOriginalFilename()),User.class);
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(password));


            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            avatarFile.transferTo(new File(uploadDir + avatarFile.getOriginalFilename()));

            User savedUser = userRepository.save(user);
            emailService.sendRegistrationEmail(user);

            return ResponseEntity.ok(savedUser);
        } catch (EmailAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while registering user", e);
        }

    }

    public String login(LoginRequest req) {
        // Implementation...
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            if (auth.isAuthenticated()) {
                return jwtService.generateToken(req.getEmail());
            } else {
                throw new BadCredentialsException("Invalid email or password");
            }
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while logging in user", e);
        }

    }
    public ResponseEntity<String> forgotPassword(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, LocalDateTime.now().plusHours(1), false);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendEmail(user.getEmail(), "Password Reset Token", "Your password reset token is: " + token);
        return ResponseEntity.ok("Password reset token sent to your email");
    }
    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElseThrow();
        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now()) || passwordResetToken.getIsUsed()) {
            return ResponseEntity.badRequest().body("Reset token has expired or is already used");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetToken.setIsUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendPasswordResetEmail(user);
        return ResponseEntity.ok("Password reset successfully");
    }

    public ResponseEntity<String> changePassword(ChangePasswordRequest request, Principal principal) {
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Old password and new password are required");
        }
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendPasswordChangeEmail(user);

        return ResponseEntity.ok("Password changed successfully");
    }







    public ResponseEntity<String> logout(String token) {
        // Implementation...
        String jwtToken = token.substring(7); // Remove "Bearer " from the token
        Optional<Session> sessionOptional = sessionRepository.findByToken(jwtToken);
        if (sessionOptional.isPresent()) {
            sessionRepository.delete(sessionOptional.get());
        }
        return ResponseEntity.ok("Logged out successfully! ");

    }

    public ResponseEntity<String> updateProfile(String name, String bio, MultipartFile avatar, Principal principal) {
        // Implementation...
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
        // Implementation...
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
