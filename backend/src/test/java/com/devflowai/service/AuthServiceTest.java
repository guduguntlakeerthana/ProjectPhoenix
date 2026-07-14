package com.devflowai.service;

import com.devflowai.dto.request.LoginRequest;
import com.devflowai.dto.response.LoginResponse;
import com.devflowai.entity.User;
import com.devflowai.repository.UserRepository;
import com.devflowai.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("keerthana@devflowai.com");
        request.setPassword("myPass12");
        User user = User.builder()
                .email("keerthana@devflowai.com")
                .password("encodedPassword")
                .fullName("Keerthana Guduguntla")
                .role("USER")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail())).thenReturn("mockTokenString");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockTokenString", response.getToken());
        assertEquals("keerthana@devflowai.com", response.getEmail());
        assertEquals("Keerthana Guduguntla", response.getFullName());
        verify(auditLogService, times(1)).logAction(eq(user), eq("USER_LOGIN"), anyString());
    }

    @Test
    public void testLoginFailurePasswordMismatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("keerthana@devflowai.com");
        request.setPassword("wrongPass");
        User user = User.builder()
                .email("keerthana@devflowai.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
