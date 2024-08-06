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
public class UsersControllerTest {

    @InjectMocks
    private UsersController usersController;

    @Mock
    private UsersService usersService;

    @Test
    public void findAllUsersShouldWork() {
        // Preparacion
        UsersDto user1 = new UsersDto();
        user1.setId("1");
        user1.setName("Luis");
        user1.setEmail("luis@example.com");

        UsersDto user2 = new UsersDto();
        user2.setId("2");
        user2.setName("Karin");
        user2.setEmail("karin@example.com");

        when(usersService.getAll(anyString())).thenReturn(Arrays.asList(user1, user2));

        //Ejecucion
        List<UsersDto> users = (List<UsersDto>) usersController.findAllUsers().getBody();

        //Validacion
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Luis", users.get(0).getName());
        assertEquals("Karin", users.get(1).getName());
    }

    @Test
    public void findByIdShouldWork() {
        //Preparacion
        UsersDto user = new UsersDto();
        user.setId("1");
        user.setName("Luis");
        user.setEmail("luis@example.com");

        when(usersService.getById(anyString(), anyString())).thenReturn(user);

        //Ejecucion
        UsersDto foundUser = (UsersDto) usersController.findUserById("1").getBody();

        //Validacion
        assertNotNull(foundUser);
        assertEquals("Luis", foundUser.getName());
        assertEquals("luis@example.com", foundUser.getEmail());
    }

    @Test
    public void updateUserShouldWork () {
        //Preparacion
        String id = "1";
        UsersDto usersDto = new UsersDto();
        usersDto.setId(id);
        usersDto.setName("Luis");
        usersDto.setEmail("luis@example.com");

        UsersDto updatedUserDto = new UsersDto();
        updatedUserDto.setId(id);
        updatedUserDto.setName("Luis Updated");
        updatedUserDto.setEmail("luis@example.com");

        when(usersService.update(any(UsersDto.class), anyString(), anyString())).thenReturn(updatedUserDto);

        //Ejecucion
        UsersDto responseUser = (UsersDto) usersController.updateUser(usersDto, id).getBody();

        //Validacion
        assertNotNull(responseUser);
        assertEquals("1", responseUser.getId());
        assertEquals("Luis Updated", responseUser.getName());
        assertEquals("luis@example.com", responseUser.getEmail());
    }

    @Test
    public void deleteUserShouldWork() {
        //Preparacion
        String id = "1";

        doNothing().when(usersService).delete(anyString(), anyString());

        //Ejecucion
        String message = usersController.deleteUser(id).getBody();

        //Validacion
        assertEquals("El usuario con ID " + id + " ha sido eliminado correctamente.", message);
    }

}