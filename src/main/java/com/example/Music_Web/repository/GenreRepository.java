package com.example.Music_Web.repository;

import com.example.Music_Web.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findByGenreNameContainingIgnoreCase(String keyword);
}
