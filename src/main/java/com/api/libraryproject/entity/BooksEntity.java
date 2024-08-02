package com.api.libraryproject.entity;

import com.api.libraryproject.util.BookStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class BooksEntity {

    @Id
    private String bookId;

    private String title;

    private String author;

    private String description;

    private BookStatus status;

    private String reservedBy;

    private String borrowedBy;

    public BooksEntity(String bookId, String title, String author, String description, BookStatus status, String reservedBy, String borrowedBy) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.status = status;
        this.reservedBy = reservedBy;
        this.borrowedBy = borrowedBy;

    }

    public BooksEntity() {
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public BookStatus getStatus() {
        return status;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public String getBorrowedBy() {
        return borrowedBy;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public void setBorrowedBy(String borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

}