package com.example.Music_Web.repository;

import com.example.Music_Web.model.Favorite;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);

    List<Favorite> findBySong(Song song);

    Optional<Favorite> findByUserAndSong(User user, Song song);

    boolean existsByUserAndSong(User user, Song song);
}
