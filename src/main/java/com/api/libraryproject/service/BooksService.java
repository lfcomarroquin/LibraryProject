package com.api.libraryproject.service;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.BooksEntity;
import com.api.libraryproject.entity.UsersEntity;
import com.api.libraryproject.exceptions.BooksException;
import com.api.libraryproject.repository.BooksRepository;
import com.api.libraryproject.repository.LibraryRepository;
import com.api.libraryproject.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.api.libraryproject.util.BookStatus;

import java.util.List;

@Service
public class BooksService {

    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private LibraryRepository libraryRepository;

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

    public BooksDto addBook(BooksDto bookDto, String userEmail) {
        UsersEntity user = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontro al usuario con el correo: " + userEmail));

        if(user.getRole() != Role.ADMIN){
            throw new BooksException("Solo los empleados de la biblioteca pueden registrar libros nuevos.");
        }

        BooksEntity bookEntity = new BooksEntity();
        bookEntity.setAuthor(bookDto.getAuthor());
        bookEntity.setTitle(bookDto.getTitle());
        bookEntity.setDescription(bookDto.getDescription());
        bookEntity.setStatus(BookStatus.DISPONIBLE);
        BooksEntity savedBookEntity = this.booksRepository.save(bookEntity);
        BooksDto savedBookDto = this.convertToDto(savedBookEntity);
        return savedBookDto;
    }

    public BooksDto updateBookStatus(String bookId, BooksDto bookDto, UsersEntity user) {
        try {
            BooksEntity bookEntity = this.booksRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Libro con ID: " + bookId + " no encontrado"));

            try{
                BookStatus.valueOf(bookDto.getStatus());
            } catch (IllegalArgumentException e){
                throw new BooksException("Ingresa un status valido. Los status validos son: DISPONIBLE, RESERVADO, PRESTADO.");
            }

            BooksDto currentBook = this.convertToDto(bookEntity);

            if (user.getRole() == Role.USER && currentBook.getStatus().equals(BookStatus.RESERVADO.toString())) {
                throw new BooksException("Los usuarios no pueden cambiar el estado de un libro ya reservado.");
            }

            if (user.getRole() == Role.USER && !bookDto.getStatus().equals(BookStatus.RESERVADO.toString())) {
                throw new BooksException("Los usuarios solo pueden reservar. Usa el status: RESERVADO.");
            }

            if (bookDto.getStatus().equals(BookStatus.RESERVADO.toString())) {
                bookDto.setReservedBy(user.getEmail());
            } else if (bookDto.getStatus().equals(BookStatus.PRESTADO.toString())) {
                if (currentBook.getStatus().equals(BookStatus.RESERVADO.toString())) {
                    bookDto.setReservedBy(currentBook.getReservedBy());
                }
                bookDto.setBorrowedBy(user.getEmail());
            }

            bookEntity.setStatus(BookStatus.valueOf(bookDto.getStatus()));
            bookEntity.setReservedBy(bookDto.getReservedBy());
            bookEntity.setBorrowedBy(bookDto.getBorrowedBy());

            BooksEntity updatedBookEntity = this.booksRepository.save(bookEntity);

            BooksDto updatedBookDto = this.convertToDto(updatedBookEntity);

            return updatedBookDto;
        } catch (BooksException e) {
            throw new BooksException(e.getMessage());
        }
    }

    public void delete(String id, String userEmail) {
        UsersEntity user = libraryRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontro al usuario con el correo: " + userEmail));

        if (user.getRole() != Role.ADMIN) {
            throw new BooksException("Solo los empleados de la biblioteca pueden eliminar libros.");
        }

        BooksEntity booksEntity = this.booksRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro un libro con el ID: " + id));

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