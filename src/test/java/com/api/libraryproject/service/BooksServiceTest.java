package com.api.libraryproject.service;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.BooksEntity;
import com.api.libraryproject.repository.BooksRepository;
import com.api.libraryproject.util.BookStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BooksServiceTest {

    @InjectMocks
    private BooksService booksService;

    @Mock
    private BooksRepository booksRepository;

    @Test
    public void getAllBooksShouldWork() {
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
    public void getBookByIdShouldWork() {
        BooksEntity bookEntity = new BooksEntity("1", "Book Title", "Author","Description", BookStatus.DISPONIBLE, null, null);

        when(booksRepository.findById("1")).thenReturn(Optional.of(bookEntity));

        BooksDto foundBook = booksService.getBookById("1");

        assertNotNull(foundBook);
        assertEquals("Book Title", foundBook.getTitle());
        assertEquals("Author", foundBook.getAuthor());
    }

    @Test
    public void addBookShouldWork() {
        BooksDto booksDto = new BooksDto(null, "Book Title", "Author", "Description", "DISPONIBLE", "user1@mail.com", "user2@mail.com");
        BooksEntity bookEntity = new BooksEntity("1", "Book Title", "Author", "Description", BookStatus.DISPONIBLE, "user1@mail.com", "user2@mail.com");

        when(booksRepository.save(any(BooksEntity.class))).thenReturn(bookEntity);

        BooksDto savedBookDto = booksService.addBook(booksDto);

        assertNotNull(savedBookDto);
        assertEquals("1", savedBookDto.getBookId());
        assertEquals("Book Title", savedBookDto.getTitle());
        assertEquals("Author", savedBookDto.getAuthor());
    }

    @Test
    public void updateBookStatusShouldWork() {
        BooksDto booksDto = new BooksDto("1", "Book Title", "Author", "Description", "DISPONIBLE", "user1@mail.com", "user2@mail.com");
        BooksEntity bookEntity = new BooksEntity("1", "Book Title", "Author", "Description", BookStatus.DISPONIBLE, "user1@mail.com", "user2@mail.com");

        when(booksRepository.findById("1")).thenReturn(Optional.of(bookEntity));
        when(booksRepository.save(any(BooksEntity.class))).thenReturn(bookEntity);

        BooksDto updatedBook = booksService.updateBookStatus("1", booksDto);

        assertNotNull(updatedBook);
        assertEquals("1", updatedBook.getBookId());
        assertEquals("Book Title", updatedBook.getTitle());
        assertEquals("Author", updatedBook.getAuthor());
    }

}