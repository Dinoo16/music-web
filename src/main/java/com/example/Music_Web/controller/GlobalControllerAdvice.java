package com.example.Music_Web.controller;

import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Genre;
import com.example.Music_Web.model.Playlist;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.PlaylistRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.UserRepository;
import com.example.Music_Web.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserRepository userRepository;

    // get user playlist
    @ModelAttribute("userPlaylists")
    public List<Playlist> addPlaylistsToModel(Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return playlistRepository.findByUser(user);
        }
        return Collections.emptyList();
    }

    @ModelAttribute("globalSongs")
    public List<Song> getGlobalSongs() {

        return songRepository.findAll();
    }

    @ModelAttribute("globalSongsOfGenre")
    public Map<Long, Integer> getGenreSongCount() {
        List<Genre> genres = genreRepository.findAll();
        Map<Long, Integer> genreSongCount = new HashMap<>();
        for (Genre genre : genres) {
            int count = songRepository.findByGenresOfSong_GenreID(genre.getGenreID()).size();
            genreSongCount.put(genre.getGenreID(), count);
        }
        return genreSongCount;
    }

    @ModelAttribute("globalArtistTotalPlays")
    public Map<Long, Integer> getArtistTotalPlays() {
        List<Artist> artists = artistRepository.findAll();
        Map<Long, Integer> artistTotalPlays = new HashMap<>();
        for (Artist artist : artists) {
            List<Song> songsOfArtist = songRepository.findByArtistsOfSong_ArtistID(artist.getArtistID());
            int totalPlays = 0;
            for (Song song : songsOfArtist) {
                totalPlays += song.getPlays();
            }
            artistTotalPlays.put(artist.getArtistID(), totalPlays);
        }
        return artistTotalPlays;
    }

    @ModelAttribute("globalArtistSongCount")
    public Map<Long, Integer> getArtistSongCount() {
        List<Artist> artists = artistRepository.findAll();
        Map<Long, Integer> artistSongCount = new HashMap<>();
        for (Artist artist : artists) {
            List<Song> songsOfArtist = songRepository.findByArtistsOfSong_ArtistID(artist.getArtistID());
            artistSongCount.put(artist.getArtistID(), songsOfArtist.size());
        }

        return artistSongCount;
    }

}
