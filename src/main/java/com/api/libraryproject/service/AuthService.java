package com.api.libraryproject.service;

import com.api.libraryproject.config.JwtService;
import com.api.libraryproject.dto.auth.AuthDto;
import com.api.libraryproject.dto.auth.LoginDto;
import com.api.libraryproject.dto.auth.RegisterDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.exceptions.RegisterException;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.util.Role;
import com.api.libraryproject.exceptions.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthDto login (final LoginDto request) {
        if (!areValidCredentials(request)) {
            throw new InvalidCredentialsException("Username o password incorrectos.");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = libraryRepository.findByEmail(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);
        return new AuthDto(token);
    }

    public AuthDto register (final RegisterDto request) {

        if(libraryRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegisterException("El email ya esta registrado. Por favor ingrese uno diferente");
        }

        UsersEntity user = new UsersEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        libraryRepository.save(user);
        return new AuthDto("El usuario ha sido creado exitosamente");
    }

    public boolean areValidCredentials(LoginDto login) {
        UsersEntity user = libraryRepository.findByEmail(login.getUsername()).orElse(null);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(login.getPassword(), user.getPassword());
    }

}