package com.api.libraryproject.service;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.repository.LibraryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @InjectMocks
    private UsersService usersService;

    @Mock
    private LibraryRepository libraryRepository;

    @Test
    public void getAllShouldWork() {
        //Preparacion
        UsersEntity user1 = new UsersEntity();
        user1.setId("1");
        user1.setName("Luis");
        user1.setEmail("luis@example.com");

        UsersEntity user2 = new UsersEntity();
        user2.setId("2");
        user2.setName("Karin");
        user2.setEmail("karin@example.com");

        when(libraryRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        //Ejecucion
        List<UsersDto> users = usersService.getAll();

        //Validacion
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Luis", users.get(0).getName());
        assertEquals("Karin", users.get(1).getName());
    }

    @Test
    public void findByIdShouldWork() {
        //Preparacion
        UsersEntity user = new UsersEntity();
        user.setId("1");
        user.setName("Luis");
        user.setEmail("luis@example.com");

        when(libraryRepository.findById("1")).thenReturn(java.util.Optional.of(user));

        //Ejecucion
        UsersDto foundUser = usersService.getById("1");

        //Validacion
        assertNotNull(foundUser);
        assertEquals("Luis", foundUser.getName());
        assertEquals("luis@example.com", foundUser.getEmail());
    }

    @Test
    public void saveStudentShouldWork() {
        //Preparacion
        UsersDto usersDto = new UsersDto(null,"Luis","luis@example.com");
        UsersEntity student = new UsersEntity();
        student.setName("Luis");
        student.setEmail("luis@example.com");

        UsersEntity savedStudent = new UsersEntity();
        savedStudent.setId("1");
        savedStudent.setName("Luis");
        savedStudent.setEmail("luis@example.com");

        when(libraryRepository.save(any(UsersEntity.class))).thenReturn(savedStudent);

        //Ejecucion
        UsersDto savedUserDto = usersService.save(usersDto);

        //Validacion
        assertNotNull(savedUserDto);
        assertEquals("1", savedUserDto.getId());
        assertEquals("Luis", savedUserDto.getName());
        assertEquals("luis@example.com", savedUserDto.getEmail());
    }

    @Test
    public void updateStudentShouldWork() {
        //Preparacion
        UsersDto usersDto = new UsersDto(null,"Luis","luis@example.com");

        UsersEntity user = new UsersEntity();
        user.setId("1");
        user.setName("Luis");
        user.setEmail("luis@example.com");

        when(libraryRepository.findById("1")).thenReturn(Optional.of(user));
        when(libraryRepository.save(any(UsersEntity.class))).thenReturn(user);

        //Ejecucion
        UsersDto updatedStudent = usersService.update(usersDto, "1");

        //Validacion
        assertNotNull(updatedStudent);
        assertEquals("1", updatedStudent.getId());
        assertEquals("Luis", updatedStudent.getName());
        assertEquals("luis@example.com", updatedStudent.getEmail());
    }

    @Test
    public void deleteStudentShouldWork() {
        //Preparacion
        UsersEntity user = new UsersEntity();
        user.setId("1");
        user.setName("Luis");
        user.setEmail("luis@example.com");

        when(libraryRepository.findById("1")).thenReturn(Optional.of(user));

        //Ejecucion
        usersService.delete("1");

        //Validacion
        verify(libraryRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDatabaseErrorOccurs() {
        //Preparacion
        String email = "test@example.com";

        //Ejecucion
        when(libraryRepository.findByEmail(email)).thenThrow(new DataAccessException("Error accediendo a la base de datos") {});

        //Validacion
        assertThrows(DataAccessException.class, () -> usersService.findUserByEmail(email));
    }

}