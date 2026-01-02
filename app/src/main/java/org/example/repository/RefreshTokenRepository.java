package org.example.repository;

import org.example.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// Data Access Object (DAO) — that means the class is responsible for interacting with the database.
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken , Integer> {
    Optional<RefreshToken> findByToken(String token);
}
