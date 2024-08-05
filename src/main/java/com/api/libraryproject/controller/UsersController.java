package com.api.libraryproject.controller;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.exceptions.ApiErrorResponse;
import com.api.libraryproject.exceptions.UsersException;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.service.UsersService;
import com.api.libraryproject.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libraryusers")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private LibraryRepository libraryRepository;

    @GetMapping("/listall")
    public ResponseEntity<?> findAllUsers() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("No se encontro al usuario con el correo: " + userDetails.getUsername()));

            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
            }

            List<UsersDto> listUsers = this.usersService.getAll();
            return ResponseEntity.ok().body(listUsers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/id")
    public ResponseEntity<?> findUserById(@PathVariable("id") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("No se encontro al usuario con el correo: " + userDetails.getUsername()));

            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
            }

            UsersDto usersDto = this.usersService.getById(id);
            return ResponseEntity.ok(usersDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateUser(@RequestBody UsersDto usersDto, @PathVariable("id") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("No se encontro al usuario con el correo: " + userDetails.getUsername()));

            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
            }

            UsersDto updatedUser = this.usersService.update(usersDto, id);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("No se encontro al usuario con el correo: " + userDetails.getUsername()));

            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
            }

            this.usersService.delete(id);
            String mensaje = "El usuario con ID " + id + " ha sido eliminado correctamente.";
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @ExceptionHandler({UsersException.class})
    public ResponseEntity<?> handleUsersException(UsersException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatus());
    }

}