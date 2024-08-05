package com.api.libraryproject.controller;

import com.api.libraryproject.dto.auth.AuthDto;
import com.api.libraryproject.dto.auth.LoginDto;
import com.api.libraryproject.dto.auth.RegisterDto;
import com.api.libraryproject.exceptions.ApiErrorResponse;
import com.api.libraryproject.exceptions.InvalidCredentialsException;
import com.api.libraryproject.exceptions.RegisterException;
import com.api.libraryproject.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDto login) {
        try {
            AuthDto authDto = this.authService.login(login);
            return ResponseEntity.ok(authDto);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        try {
            AuthDto authDto = this.authService.register(dto);
            return ResponseEntity.ok(authDto);
        } catch (RegisterException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @ExceptionHandler({ InvalidCredentialsException.class })
    public ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatus());
    }

}