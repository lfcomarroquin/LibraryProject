package com.api.libraryproject.service;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private LibraryRepository libraryRepository;

    public List<UsersDto> getAll() {
        return this.libraryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UsersDto getById(String id) {
        return this.libraryRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public UsersDto save(UsersDto school) {
        UsersEntity entity = new UsersEntity();
        entity.setName(school.getName());
        entity.setEmail(school.getEmail());
        UsersEntity entitySaved = this.libraryRepository.save(entity);
        UsersDto saved = this.toDto(entitySaved);
        return saved;
    }

    public UsersDto update(UsersDto school, String id) {
        UsersEntity entity = this.libraryRepository.findById(id)
                .orElse(null);
        entity.setName(school.getName());
        entity.setEmail(school.getEmail());
        UsersEntity entitySaved = this.libraryRepository.save(entity);
        UsersDto saved = this.toDto(entitySaved);
        return saved;
    }

    public void delete(String id) {
        UsersEntity entity = this.libraryRepository.findById(id)
                .orElse(null);
        this.libraryRepository.delete(entity);
    }

    private UsersDto toDto(UsersEntity entity) {
        return new UsersDto(entity.getId(), entity.getName(), entity.getEmail());
    }

}
