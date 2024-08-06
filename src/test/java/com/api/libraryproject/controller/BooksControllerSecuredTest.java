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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
        user.setName("admin");
        user.setEmail("admin@library.com");
        user.setRole(Role.ADMIN);
        Mockito.when(authentication.getPrincipal()).thenReturn(new User(user.getEmail(), "", new ArrayList<>()));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
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

        when(booksService.addBook(any(BooksDto.class), anyString())).thenReturn(savedBookDto);

        BooksDto savedBook = (BooksDto) booksController.createBook(bookDto).getBody();

        assertNotNull(savedBook);
        assertEquals("1", savedBook.getBookId());
        assertEquals("Book Title", savedBook.getTitle());
        assertEquals("Author", savedBook.getAuthor());
    }

    @Test
    public void updateBookStatusShouldWork() {
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

        UsersEntity userEntity = new UsersEntity();
        userEntity.setName("admin");
        userEntity.setEmail("admin@library.com");
        userEntity.setRole(Role.ADMIN);

        UserDetails mockUserDetails = User.builder()
                .username("admin@library.com")
                .password("")
                .authorities(new ArrayList<>())
                .build();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));
        when(libraryRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(userEntity));
        when(booksService.updateBookStatus(eq(id), any(BooksDto.class), any(UsersEntity.class))).thenReturn(updatedBookDto);

        BooksDto responseBook = (BooksDto) booksController.updateBookStatus(id, bookDto).getBody();

        assertNotNull(responseBook);
        assertEquals("1", responseBook.getBookId());
        assertEquals("Book Title Updated", responseBook.getTitle());
        assertEquals("Author Updated", responseBook.getAuthor());
        assertEquals("RESERVADO", responseBook.getStatus());
    }

}