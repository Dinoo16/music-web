package com.example.Music_Web.repository;

import com.example.Music_Web.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByAlbumNameContainingIgnoreCase(String keyword);

    List<Album> findByArtistsOfAlbum_ArtistID(Long id);

    List<Album> findDistinctBySongsOfAlbum_ArtistsOfSong_ArtistID(Long id);
}
