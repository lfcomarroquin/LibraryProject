package com.api.libraryproject.controller;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.service.BooksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BooksControllerUnsecuredTest {

    @InjectMocks
    private BooksController booksController;

    @Mock
    private BooksService booksService;

    @Test
    public void findAllBooksShouldWork() {
        BooksDto book1 = new BooksDto();
        book1.setBookId("1");
        book1.setTitle("Book Title 1");
        book1.setAuthor("Author 1");

        BooksDto book2 = new BooksDto();
        book2.setBookId("2");
        book2.setTitle("Book Title 2");
        book2.setAuthor("Author 2");

        when(booksService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        List<BooksDto> books = booksController.findAllBooks().getBody();

        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Book Title 1", books.get(0).getTitle());
        assertEquals("Author 2", books.get(1).getAuthor());
    }

    @Test
    public void findBookByIdShouldWork() {
        BooksDto book = new BooksDto();
        book.setBookId("1");
        book.setTitle("Book Title");
        book.setAuthor("Author");

        when(booksService.getBookById("1")).thenReturn(book);

        BooksDto foundBook = (BooksDto) booksController.findBookById("1").getBody();

        assertNotNull(foundBook);
        assertEquals("Book Title", foundBook.getTitle());
        assertEquals("Author", foundBook.getAuthor());
    }

}