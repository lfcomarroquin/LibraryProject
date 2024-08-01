package com.api.libraryproject.controller;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.service.BooksService;
import com.api.libraryproject.util.BookStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BooksService booksService;

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
        BooksDto savedBook = this.booksService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BooksDto> updateBookStatus(@PathVariable("id") String id, @RequestBody BooksDto book) {
        BooksDto updatedBook = this.booksService.updateBookStatus(id, BookStatus.valueOf(book.getStatus()));
        return ResponseEntity.ok(updatedBook);
    }

}