package com.api.libraryproject.controller;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.exceptions.ApiErrorResponse;
import com.api.libraryproject.exceptions.UsersException;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

            String userEmail = userDetails.getUsername();

            List<UsersDto> listUsers = this.usersService.getAll(userEmail);
            return ResponseEntity.ok().body(listUsers);
        }
        catch (UsersException | UsernameNotFoundException e) {
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

            String userEmail = userDetails.getUsername();

            UsersDto usersDto = this.usersService.getById(id, userEmail);
            return ResponseEntity.ok(usersDto);
        }
        catch (UsersException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{email}/email")
    public ResponseEntity<?> findUserByEmail(@PathVariable("email") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String userEmail = userDetails.getUsername();

            UsersDto usersDto = this.usersService.getByEmail(id, userEmail);
            return ResponseEntity.ok(usersDto);
        }
        catch (UsersException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateUser(@RequestBody UsersDto usersDto, @PathVariable("id") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String userEmail = userDetails.getUsername();

            UsersDto updatedUser = this.usersService.update(usersDto, id, userEmail);
            return ResponseEntity.ok(updatedUser);
        }
        catch (UsersException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String userEmail = userDetails.getUsername();

            this.usersService.delete(id, userEmail);
            String mensaje = "El usuario con ID " + id + " ha sido eliminado correctamente.";
            return ResponseEntity.ok(mensaje);
        }
        catch (UsersException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ExceptionHandler({UsersException.class})
    public ResponseEntity<?> handleUsersException(UsersException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, apiErrorResponse.getStatus());
    }

}