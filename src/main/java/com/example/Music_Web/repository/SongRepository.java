package com.example.Music_Web.repository;

import com.example.Music_Web.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByTitleContainingIgnoreCase(String keyword);

    List<Song> findByArtistsOfSong_ArtistID(Long artistId);

    List<Song> findByGenresOfSong_GenreID(Long genreId);

}
