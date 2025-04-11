package com.example.Music_Web.repository;

import com.example.Music_Web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    default User getByUsername(String username) {
        return findByUsername(username).orElse(null);
    }
}
