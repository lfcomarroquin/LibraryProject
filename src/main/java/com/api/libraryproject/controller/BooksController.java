package com.api.libraryproject.controller;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.service.BooksService;
import com.api.libraryproject.util.BookStatus;
import com.api.libraryproject.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BooksService booksService;

    @Autowired
    private LibraryRepository libraryRepository;

    @GetMapping("/allbooks")
    public ResponseEntity<List<BooksDto>> findAllBooks() {
        List<BooksDto> listBooks = this.booksService.getAllBooks();
        return ResponseEntity.ok(listBooks);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findBookById(@PathVariable("id") String id) {
        try {
            BooksDto book = this.booksService.getBookById(id);
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<?> findBooksByTitle(@PathVariable("title") String title){
        try {
            List<BooksDto> books = this.booksService.getBooksByTitle(title);
            return ResponseEntity.ok(books);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<?> findBooksByAuthor(@PathVariable("author") String author) {
        try {
            List<BooksDto> books = this.booksService.getBooksByAuthor(author);
            return ResponseEntity.ok(books);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/NewBook")
    public ResponseEntity<?> createBook(@RequestBody BooksDto book) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se encontró al usuario con el correo: " + userDetails.getUsername()
                ));

        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo los empleados de la biblioteca pueden registrar libros nuevos."
            );
        }

        BooksDto savedBook = this.booksService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BooksDto> updateBookStatus(
            @PathVariable("id") String id,
            @RequestBody BooksDto book) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String userEmail = userDetails.getUsername();

        UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new UsernameNotFoundException("No se encontró al usuario con el correo: " + userDetails.getUsername())
                );

        try {
            BookStatus.valueOf(book.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ingresa un status valido. Los status válidos son DISPONIBLE, RESERVADO, o PRESTADO.");
        }

        BooksDto currentBook = this.booksService.getBookById(id);

        if (user.getRole() == Role.USER && currentBook.getStatus().equals(BookStatus.RESERVADO.toString())) {
            throw new IllegalArgumentException("Los usuarios no pueden cambiar el estado de un libro ya reservado.");
        }

        if (user.getRole() == Role.USER && !book.getStatus().equals(BookStatus.RESERVADO.toString())) {
            throw new IllegalArgumentException("Los usuarios solo pueden reservar. Usa el status: RESERVADO.");
        }

        if (book.getStatus().equals(BookStatus.RESERVADO.toString())) {
            book.setReservedBy(userEmail);
        } else if (book.getStatus().equals(BookStatus.PRESTADO.toString())) {
            if (currentBook.getStatus().equals(BookStatus.RESERVADO.toString())) {
                book.setReservedBy(currentBook.getReservedBy());
            }
            book.setBorrowedBy(userEmail);
        }

        BooksDto updatedBook = this.booksService.updateBookStatus(id, book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}/deletebook")
    public ResponseEntity<String> deleteBook(@PathVariable("id") String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new UsernameNotFoundException("No se encontró al usuario con el correo: " + userDetails.getUsername())
                );

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Solo los empleados de la biblioteca pueden borrar libros.");
        }

        try {
            this.booksService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("No se encontró un libro con el ID: " + id);
        }

        String message = "El libro con ID " + id + " ha sido eliminado correctamente.";
        return ResponseEntity.ok(message);
    }

}