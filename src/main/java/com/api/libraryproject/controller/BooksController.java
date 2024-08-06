package com.api.libraryproject.controller;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.exceptions.BooksException;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.service.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String userEmail = userDetails.getUsername();

            BooksDto savedBook = this.booksService.addBook(book, userEmail);
            return new ResponseEntity<>(savedBook, HttpStatus.CREATED);

        } catch (BooksException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookStatus(
            @PathVariable("id") String id,
            @RequestBody BooksDto book) {

        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String userEmail = userDetails.getUsername();

            UsersEntity user = libraryRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("No se encontró al usuario con el correo: " + userDetails.getUsername()));

            BooksDto updatedBook = this.booksService.updateBookStatus(id, book, user);
            return ResponseEntity.ok(updatedBook);
        }
        catch (BooksException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/deletebook")
    public ResponseEntity<?> deleteBook(@PathVariable("id") String id) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String userEmail = userDetails.getUsername();

            this.booksService.delete(id, userEmail);
            return ResponseEntity.ok("El libro con ID " + id + " ha sido eliminado correctamente.");
        }
        catch (BooksException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}