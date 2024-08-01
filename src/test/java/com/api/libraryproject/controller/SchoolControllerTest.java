package com.api.libraryproject.controller;

import com.api.libraryproject.dto.UsersDto;
import com.api.libraryproject.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchoolControllerTest {

    @InjectMocks
    private SchoolController schoolController;

    @Mock
    private UsersService usersService;

    @Test
    public void findAllStudentsShouldWork() {
        //Preparacion
        UsersDto user1 = new UsersDto();
        user1.setId("1");
        user1.setName("Luis");
        user1.setEmail("luis@example.com");

        UsersDto user2 = new UsersDto();
        user2.setId("2");
        user2.setName("Karin");
        user2.setEmail("karin@example.com");

        when(usersService.getAll()).thenReturn(Arrays.asList(user1, user2));

        //Ejecucion
        List<UsersDto> students = schoolController.findAllStudents().getBody();

        //Validacion
        assertNotNull(students);
        assertEquals(2, students.size());
        assertEquals("Luis", students.get(0).getName());
        assertEquals("Karin", students.get(1).getName());
    }

    @Test
    public void findByIdShouldWork() {
        //Preparacion
        UsersDto user = new UsersDto();
        user.setId("1");
        user.setName("Luis");
        user.setEmail("luis@example.com");

        when(usersService.getById("1")).thenReturn(user);

        //Ejecucion
        UsersDto foundUser = schoolController.findById("1").getBody();

        //Validacion
        assertNotNull(foundUser);
        assertEquals("Luis", foundUser.getName());
        assertEquals("luis@example.com", foundUser.getEmail());
    }

    @Test
    public void createStudentShouldWork() {
        //Preparacion
        UsersDto studentDto = new UsersDto();
        studentDto.setName("Luis");
        studentDto.setEmail("luis@example.com");

        UsersDto savedStudentDto = new UsersDto();
        savedStudentDto.setId("1");
        savedStudentDto.setName("Luis");
        savedStudentDto.setEmail("luis@example.com");

        when(usersService.save(studentDto)).thenReturn(savedStudentDto);

        //Ejecucion
        UsersDto savedStudent = schoolController.saveStudent(studentDto).getBody();

        //Validacion
        assertNotNull(savedStudent);
        assertEquals("1", savedStudent.getId());
        assertEquals("Luis", savedStudent.getName());
        assertEquals("luis@example.com", savedStudent.getEmail());
    }

    @Test
    public void updateStudentShouldWork () {
        //Preparacion
        String id = "1";
        UsersDto studentDto = new UsersDto();
        studentDto.setId(id);
        studentDto.setName("Luis");
        studentDto.setEmail("luis@example.com");

        UsersDto updatedStudentDto = new UsersDto();
        updatedStudentDto.setId(id);
        updatedStudentDto.setName("Luis Updated");
        updatedStudentDto.setEmail("luis@example.com");

        when(usersService.update(studentDto, id)).thenReturn(updatedStudentDto);

        //Ejecucion
        UsersDto responseStudent = schoolController.updateStudent(studentDto, id).getBody();

        //Validacion
        assertNotNull(responseStudent);
        assertEquals("1", responseStudent.getId());
        assertEquals("Luis Updated", responseStudent.getName());
        assertEquals("luis@example.com", responseStudent.getEmail());
    }

    @Test
    public void deleteStudentShouldWork() {
        //Preparacion
        String id = "1";

        //Ejecucion
        schoolController.deleteStudent(id);

        //Validacion
        verify(usersService, times(1)).delete(id);
    }

}