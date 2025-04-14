package com.example.Music_Web.repository;

import java.util.List;
import com.example.Music_Web.model.User;
import com.example.Music_Web.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUser(User user);

}
