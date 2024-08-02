package com.api.libraryproject.repository;

import com.api.libraryproject.dto.BooksDto;
import com.api.libraryproject.entity.BooksEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends MongoRepository<BooksEntity, String> {

    List<BooksDto> findByTitle(String title);

    List<BooksDto> findByAuthor(String author);

}