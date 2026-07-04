package com.devflowai.controller;

import com.devflowai.dto.request.PasswordChangeRequest;
import com.devflowai.dto.request.ProfileUpdateRequest;
import com.devflowai.dto.request.RegisterRequest;
import com.devflowai.dto.response.UserResponse;
import com.devflowai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4300")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication
    ) {
        return userService.updateProfile(request, authentication.getName());
    }

    @PutMapping("/password")
    public String changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication
    ) {
        userService.changePassword(request, authentication.getName());
        return "Password updated successfully";
    }
}