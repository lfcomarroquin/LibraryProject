package com.api.libraryproject.repository;

import com.api.libraryproject.entity.BooksEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends MongoRepository<BooksEntity, String> {

}
