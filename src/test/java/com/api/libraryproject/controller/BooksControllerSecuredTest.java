package com.api.libraryproject.controller;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.service.BooksService;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BooksControllerSecuredTest {

    @InjectMocks
    private BooksController booksController;

    @Mock
    private BooksService booksService;

    @Mock
    private LibraryRepository libraryRepository;

    @BeforeEach
    public void setUp(){
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        UsersEntity user = new UsersEntity();
        user.setEmail("admin@library.com");
        user.setRole(Role.ADMIN);
        Mockito.when(authentication.getPrincipal()).thenReturn(new User(user.getEmail(), "", new ArrayList<>()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(libraryRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    public void createBookShouldWork() {
        BooksDto bookDto = new BooksDto();
        bookDto.setTitle("Book Title");
        bookDto.setAuthor("Author");

        BooksDto savedBookDto = new BooksDto();
        savedBookDto.setBookId("1");
        savedBookDto.setTitle("Book Title");
        savedBookDto.setAuthor("Author");

        when(booksService.addBook(bookDto)).thenReturn(savedBookDto);

        BooksDto savedBook = booksController.createBook(bookDto).getBody();

        assertNotNull(savedBook);
        assertEquals("1", savedBook.getBookId());
        assertEquals("Book Title", savedBook.getTitle());
        assertEquals("Author", savedBook.getAuthor());
    }

    @Test
    public void updateBookStatusShouldWork () {
        // Asumiendo que la configuración de seguridad y roles está realizada para las pruebas
        String id = "1";
        BooksDto bookDto = new BooksDto();
        bookDto.setBookId(id);
        bookDto.setTitle("Book Title");
        bookDto.setAuthor("Author");
        bookDto.setStatus("RESERVADO");

        BooksDto updatedBookDto = new BooksDto();
        updatedBookDto.setBookId(id);
        updatedBookDto.setTitle("Book Title Updated");
        updatedBookDto.setAuthor("Author Updated");
        updatedBookDto.setStatus("RESERVADO");

        when(booksService.updateBookStatus(id, bookDto)).thenReturn(updatedBookDto);

        BooksDto responseBook = booksController.updateBookStatus(id, bookDto).getBody();

        assertNotNull(responseBook);
        assertEquals("1", responseBook.getBookId());
        assertEquals("Book Title Updated", responseBook.getTitle());
        assertEquals("Author Updated", responseBook.getAuthor());
        assertEquals("RESERVADO", responseBook.getStatus());
    }
}