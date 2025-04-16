package com.example.Music_Web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.UserRepository;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;

import com.example.Music_Web.model.Album;
import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Genre;
import com.example.Music_Web.model.Song;

@Controller
@RequestMapping("/admin") // This makes the base path '/admin' for all methods in this controller
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SongRepository songRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    AlbumRepository albumRepository;

    // For admin page (with all controls)
    @GetMapping(value = "/list")
    public String getAllSongs(Model model) {

        // Dữ liệu cho tab artist
        List<Artist> artists = artistRepository.findAll();
        model.addAttribute("artists", artists);
        Map<Long, Integer> artistTotalPlays = new HashMap<>();
        Map<Long, Integer> artistSongCount = new HashMap<>();
        for (Artist artist : artists) {
            List<Song> songsOfArtist = songRepository.findByArtistsOfSong_ArtistID(artist.getArtistID());
            int totalPlays = 0;
            for (Song song : songsOfArtist) {
                totalPlays += song.getPlays();
            }
            artistTotalPlays.put(artist.getArtistID(), totalPlays);
            artistSongCount.put(artist.getArtistID(), songsOfArtist.size());
        }
        model.addAttribute("artistTotalPlays", artistTotalPlays);
        model.addAttribute("artistSongCount", artistSongCount);

        // Dữ liệu cho tab genre
        List<Genre> genres = genreRepository.findAll();
        model.addAttribute("genres", genres);
        Map<Long, Integer> genreSongCount = new HashMap<>();
        for (Genre genre : genres) {
            List<Song> songsOfGenre = songRepository.findByGenresOfSong_GenreID(genre.getGenreID());
            genreSongCount.put(genre.getGenreID(), songsOfGenre.size());
        }
        model.addAttribute("genreSongCount", genreSongCount);
        // Dữ liệu cho tab album
        List<Album> albums = albumRepository.findAll();
        model.addAttribute("albums", albums);

        // Dữ liệu của tab song
        List<Song> songs = songRepository.findAll();
        model.addAttribute("songs", songs);
        model.addAttribute("activeTab", "song");
        return "pages/adminPage/upload";
    }

}