package com.api.libraryproject.exceptions;

public class BooksException extends IllegalArgumentException{

    public BooksException(String booksMessageException){
        super(booksMessageException);
    }

}