package com.api.libraryproject.service;

import com.api.libraryproject.config.JwtService;
import com.api.libraryproject.dto.auth.AuthDto;
import com.api.libraryproject.dto.auth.LoginDto;
import com.api.libraryproject.dto.auth.RegisterDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.exceptions.InvalidCredentialsException;
import com.api.libraryproject.exceptions.RegisterException;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private UsersEntity user;

    @BeforeEach
    public void setup() {
        user = new UsersEntity();
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setPassword("testPassword");
        user.setRole(Role.USER);
    }

    @Test
    void loginWithValidCredentialsShouldReturnToken() {
        // Given
        String expectedToken = "token";
        LoginDto request = new LoginDto(user.getEmail(), user.getPassword());

        given(libraryRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtService.getToken(any())).willReturn(expectedToken);

        // When
        AuthDto result = authService.login(request);

        // Then
        assertEquals(expectedToken, result.getToken());
    }

    @Test
    void loginWithInvalidCredentialsShouldThrowException() {
        // Given
        LoginDto request = new LoginDto(user.getEmail(), "wrongPassword");

        given(libraryRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void registerWithValidDetailsShouldReturnSuccessMessage() {
        // Given
        RegisterDto request = new RegisterDto(user.getName(), user.getEmail(), user.getPassword());
        String expectedMessage = "El usuario ha sido creado exitosamente";

        given(libraryRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(user.getPassword())).willReturn("encodedPassword");

        // When
        AuthDto result = authService.register(request);

        // Then
        assertEquals(expectedMessage, result.getToken());
    }

    @Test
    void registerWithExistingUserShouldThrowException() {
        // Given
        RegisterDto request = new RegisterDto(user.getName(), user.getEmail(), user.getPassword());

        given(libraryRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // When & Then
        assertThrows(RegisterException.class, () -> authService.register(request));
    }

}