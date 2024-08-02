package com.api.libraryproject.service;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.BooksEntity;
import com.api.libraryproject.repository.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api.libraryproject.util.BookStatus;

import java.util.List;

@Service
public class BooksService {

    @Autowired
    private BooksRepository booksRepository;

    public List<BooksDto> getAllBooks() {
        return booksRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public BooksDto getBookById(String bookId) {
        return this.booksRepository.findById(bookId)
                .map(this::convertToDto)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro un libro con el ID: " + bookId));
    }

    public List<BooksDto> getBooksByTitle(String title) {
        List<BooksDto> books = booksRepository.findByTitle(title);
        if(books.isEmpty()){
            throw new IllegalArgumentException("No se encontraron libros con el titulo: " + title);
        }
        return books;
    }

    public List<BooksDto> getBooksByAuthor(String author) {
        List<BooksDto> books = booksRepository.findByAuthor(author);
        if(books.isEmpty()){
            throw new IllegalArgumentException("No se encontraron libros del autor: " + author);
        }
        return books;
    }

    public BooksDto addBook(BooksDto bookDto) {
        BooksEntity bookEntity = new BooksEntity();
        bookEntity.setAuthor(bookDto.getAuthor());
        bookEntity.setTitle(bookDto.getTitle());
        bookEntity.setDescription(bookDto.getDescription());
        bookEntity.setStatus(BookStatus.DISPONIBLE);
        BooksEntity savedBookEntity = this.booksRepository.save(bookEntity);
        BooksDto savedBookDto = this.convertToDto(savedBookEntity);
        return savedBookDto;
    }

    public BooksDto updateBookStatus(String bookId, BooksDto bookDto) {
        BooksEntity bookEntity = this.booksRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro con ID: " + bookId + " no encontrado"));

        bookEntity.setStatus(BookStatus.valueOf(bookDto.getStatus()));
        bookEntity.setReservedBy(bookDto.getReservedBy());
        bookEntity.setBorrowedBy(bookDto.getBorrowedBy());

        BooksEntity updatedBookEntity = this.booksRepository.save(bookEntity);

        BooksDto updatedBookDto = this.convertToDto(updatedBookEntity);

        return updatedBookDto;
    }

    public void delete(String id) {
        BooksEntity booksEntity = this.booksRepository.findById(id)
                .orElse(null);
        this.booksRepository.delete(booksEntity);
    }

    private BooksDto convertToDto(BooksEntity booksEntity) {
        return new BooksDto(
                booksEntity.getBookId(),
                booksEntity.getTitle(),
                booksEntity.getAuthor(),
                booksEntity.getDescription(),
                booksEntity.getStatus().toString(),
                booksEntity.getReservedBy(),
                booksEntity.getBorrowedBy()
        );
    }

}