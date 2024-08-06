
package com.api.libraryproject.service;
import com.api.libraryproject.repository.LibraryRepository;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.BooksEntity;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.repository.BooksRepository;
import com.api.libraryproject.util.BookStatus;
import com.api.libraryproject.util.Role;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BooksServiceTest {

    @InjectMocks
    private BooksService booksService;

    @Mock
    private BooksRepository booksRepository;

    @Test
    public void getAllBooksShouldReturnAllBooksWhenBooksExist() {
        BooksEntity book1 = new BooksEntity("1", "Book Title 1", "Author 1", "Some description", BookStatus.PRESTADO, null, null);
        BooksEntity book2 = new BooksEntity("2", "Book Title 2", "Author 2", "Some other description", BookStatus.DISPONIBLE, null, null);

        when(booksRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<BooksDto> books = booksService.getAllBooks();

        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Book Title 1", books.get(0).getTitle());
        assertEquals("Author 2", books.get(1).getAuthor());
    }

    @Test
    public void getAllBooksShouldReturnEmptyListWhenNoBooksExist() {
        when(booksRepository.findAll()).thenReturn(new ArrayList<>());

        List<BooksDto> books = booksService.getAllBooks();

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    public void getBookByIdShouldWork() {
        BooksEntity bookEntity = new BooksEntity("1", "Book Title", "Author","Description", BookStatus.DISPONIBLE, null, null);

        when(booksRepository.findById("1")).thenReturn(Optional.of(bookEntity));

        BooksDto foundBook = booksService.getBookById("1");

        assertNotNull(foundBook);
        assertEquals("Book Title", foundBook.getTitle());
        assertEquals("Author", foundBook.getAuthor());
    }

    @Test
    public void updateBookStatusShouldWork() {
        BooksDto booksDto = new BooksDto("1", "Book Title", "Author", "Description", "DISPONIBLE", "user1@mail.com", "user2@mail.com");
        BooksEntity bookEntity = new BooksEntity("1", "Book Title", "Author", "Description", BookStatus.DISPONIBLE, "user1@mail.com", "user2@mail.com");
        UsersEntity userAdmin = new UsersEntity("adminId", "admin","admin@library.com", "", Role.ADMIN);

        when(booksRepository.findById("1")).thenReturn(Optional.of(bookEntity));
        when(booksRepository.save(any(BooksEntity.class))).thenReturn(bookEntity);

        BooksDto updatedBook = booksService.updateBookStatus("1", booksDto, userAdmin);

        assertNotNull(updatedBook);
        assertEquals("1", updatedBook.getBookId());
        assertEquals("Book Title", updatedBook.getTitle());
        assertEquals("Author", updatedBook.getAuthor());
    }

}