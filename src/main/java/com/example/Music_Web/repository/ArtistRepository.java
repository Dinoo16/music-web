package com.example.Music_Web.repository;

import com.example.Music_Web.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByArtistNameContainingIgnoreCase(String keyword);
}
