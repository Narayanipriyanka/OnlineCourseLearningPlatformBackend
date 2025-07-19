package com.Basics.onlineCoursePlatform.controller;

import com.Basics.onlineCoursePlatform.DTO.UserDTO;
import com.Basics.onlineCoursePlatform.DTO.LoginRequest;

import com.Basics.onlineCoursePlatform.entity.ChangePasswordRequest;
import com.Basics.onlineCoursePlatform.entity.ResetPasswordRequest;
import com.Basics.onlineCoursePlatform.entity.Role;
import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(summary = "Register new user")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> register(
            @RequestPart("avatar") MultipartFile avatarFile,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("bio") String bio,
            @RequestParam("role") Role role) {
        return authService.register(avatarFile,name,email,password,bio,role);
    }




    @Operation(summary = "Login a user")
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {
         return authService.login(req);
    }

    @Operation(summary = "Forgot password Send password reset token")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(Principal principal) {
        return authService.forgotPassword(principal);
    }

    @Operation(summary = "Reset password using token")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }
    @Operation(summary = "Change password")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        return authService.changePassword(request, principal);
    }


    @Operation(summary = "Logout user")
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
       return authService.logout(token);
    }


    @Operation(summary = "Update user profile")
    @PutMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateProfile(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "bio", required = false) String bio,
                                                @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                                Principal principal) {
       return authService.updateProfile(name,bio,avatar,principal);
    }


}
