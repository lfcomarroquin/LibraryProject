package com.api.libraryproject.controller;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/school")
public class SchoolController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/query")
    public ResponseEntity<List<UsersDto>> findAllStudents() {
        List<UsersDto> listStudents = this.usersService.getAll();
        return ResponseEntity.ok(listStudents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> findById(@PathVariable("id") String id) {
        UsersDto student = this.usersService.getById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/")
    public ResponseEntity<UsersDto> saveStudent(@RequestBody UsersDto studentDto) {
        UsersDto savedStudent = this.usersService.save(studentDto);
        return ResponseEntity.ok(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDto> updateStudent(@RequestBody UsersDto studentDto, @PathVariable("id") String id) {
        UsersDto updatedStudent = this.usersService.update(studentDto, id);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") String id) {
        this.usersService.delete(id);
        String mensaje = "El usuario con ID " + id + " ha sido eliminado correctamente.";
        return ResponseEntity.ok(mensaje);
    }

}
