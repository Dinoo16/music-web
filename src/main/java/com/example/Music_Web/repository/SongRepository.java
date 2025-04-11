package com.example.Music_Web.repository;


import com.example.Music_Web.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}
