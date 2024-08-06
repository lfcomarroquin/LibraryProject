package com.api.libraryproject.service;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.exceptions.UsersException;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private LibraryRepository libraryRepository;

    public List<UsersDto> getAll(String userEmail) {
        UsersEntity users = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsersException("No se encontro un usuario con el email: " + userEmail));

        if (users.getRole() != (Role.ADMIN)) {
            throw new UsersException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
        }

        return this.libraryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UsersDto getById(String userId, String userEmail) {
        UsersEntity user = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontro al usuario con el correo: " + userEmail));

        if (user.getRole() != Role.ADMIN) {
            throw new UsersException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
        }

        return this.libraryRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro un usuario con el ID: " + userId));
    }

    public UsersDto getByEmail(String email, String userEmail) {
        UsersEntity user = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsersException("No se encontro un usuario con el email: " + userEmail));

        if (user.getRole() != Role.ADMIN) {
            throw new UsersException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
        }

        return this.libraryRepository.findByEmail(email)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro un usuario con el email: " + userEmail));
    }

    public UsersDto update(UsersDto usersDto, String userId, String userEmail) {
        UsersEntity user = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontro al usuario con el correo: " + userEmail));

        if (user.getRole() != Role.ADMIN) {
            throw new UsersException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
        }

        UsersEntity entity = this.libraryRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro un usuario con el ID: " + userId));

        entity.setName(usersDto.getName());
        entity.setEmail(usersDto.getEmail());
        UsersEntity entitySaved = this.libraryRepository.save(entity);
        UsersDto saved = this.toDto(entitySaved);
        return saved;
    }

    public void delete(String id, String userEmail) {
        UsersEntity user = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontro al usuario con el correo: " + userEmail));

        if (user.getRole() != Role.ADMIN) {
            throw new UsersException("Solo los empleados de la biblioteca pueden acceder a la lista de usuarios.");
        }

        UsersEntity usersEntity = this.libraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro un usuario con el ID: " + id));

        this.libraryRepository.delete(usersEntity);
    }

    private UsersDto toDto(UsersEntity entity) {
        return new UsersDto(entity.getId(), entity.getName(), entity.getEmail());
    }

    public boolean isAdmin(String email) {
        UsersEntity user = libraryRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        return user.getRole() == Role.ADMIN;
    }

}