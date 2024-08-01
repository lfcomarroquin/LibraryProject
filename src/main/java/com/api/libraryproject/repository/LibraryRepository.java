package com.api.libraryproject.repository;

import com.api.libraryproject.entity.UsersEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryRepository extends MongoRepository<UsersEntity, String> {

    Optional<UsersEntity> findByEmail(String email);

}
