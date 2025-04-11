package com.example.Music_Web.repository;

import com.example.Music_Web.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
