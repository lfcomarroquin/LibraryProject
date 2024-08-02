package com.api.libraryproject.controller;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.service.BooksService;
import com.api.libraryproject.util.BookStatus;
import com.api.libraryproject.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<BooksDto> findBookById(@PathVariable("id") String id) {
        BooksDto book = this.booksService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/NewBook")
    public ResponseEntity<BooksDto> createBook(@RequestBody BooksDto book) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new UsernameNotFoundException("No se encontró al usuario con el correo: " + userDetails.getUsername())
                );

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Solo los usuarios con rol de ADMIN pueden crear libros.");
        }

        BooksDto savedBook = this.booksService.addBook(book);
        return ResponseEntity.ok(savedBook);
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

}