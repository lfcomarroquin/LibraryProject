package com.api.libraryproject.service;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.BooksEntity;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.repository.BooksRepository;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.util.BookStatus;
import com.api.libraryproject.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class BooksServiceSecuredTest {

    @InjectMocks
    private BooksService booksService;

    @Mock
    private BooksRepository booksRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        UsersEntity adminUser = new UsersEntity();
        adminUser.setId("admin");
        adminUser.setName("Admin");
        adminUser.setEmail("admin@library.com");
        adminUser.setRole(Role.ADMIN);

        when(libraryRepository.findByEmail(anyString())).thenReturn(Optional.of(adminUser));
    }

    @Test
    public void addBookShouldWork() {
        BooksDto booksDto = new BooksDto(null, "Book Title", "Author", "Description", "DISPONIBLE", "user1@mail.com", "user2@mail.com");
        BooksEntity bookEntity = new BooksEntity("1", "Book Title", "Author", "Description", BookStatus.DISPONIBLE, "user1@mail.com", "user2@mail.com");

        when(booksRepository.save(any(BooksEntity.class))).thenReturn(bookEntity);

        BooksDto savedBookDto;
        savedBookDto = booksService.addBook(booksDto, "user1@mail.com");

        assertNotNull(savedBookDto);
        assertEquals("1", savedBookDto.getBookId());
        assertEquals("Book Title", savedBookDto.getTitle());
        assertEquals("Author", savedBookDto.getAuthor());
    }

}