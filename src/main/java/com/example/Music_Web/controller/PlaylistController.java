package com.example.Music_Web.controller;

import com.example.Music_Web.model.Playlist;
import com.example.Music_Web.exception.PlaylistNotFoundException;
import com.example.Music_Web.model.Album;
import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Genre;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.PlaylistRepository;
import com.example.Music_Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

//User
//1.	Create Playlist
//2.	Remove song from playlist 
//3.	Add song to playlist
//4.	Delete Playlist

@Controller
@RequestMapping(value = "/playlist")
public class PlaylistController {
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    SongRepository songRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/list")
    public String getAllUserPlaylists(Model model) {
        return "pages/userPage/myplaylist";
    }

    @GetMapping("/detail/{id}")
    public String getPlaylistDetail(@PathVariable Long id, Model model) throws PlaylistNotFoundException {

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Invalid playlist ID: " + id));

        // Get recommended songs (songs not in this playlist)
        List<Song> allSongs = songRepository.findAll();
        List<Song> recommendedSongs = allSongs.stream()
                .filter(song -> !playlist.getSongs().contains(song))
                .collect(Collectors.toList());

        model.addAttribute("recommendedSongs", recommendedSongs);
        model.addAttribute("playlist", playlist);
        model.addAttribute("songs", songRepository.findAll());
        return "pages/userPage/myplaylistDetail";
    }

    @PostMapping("/add")
    public String addPlaylist(@ModelAttribute Playlist playlist, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        playlist.setUser(user);
        playlistRepository.save(playlist);
        return "redirect:/playlist/list";
    }

    @GetMapping("/update/{id}")
    public String updatePlaylist(@PathVariable Long id, Model model) throws PlaylistNotFoundException {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Invalid playlist ID: " + id));
        model.addAttribute("playlist", playlist);
        return "pages/adminPage/editPlaylist";
    }

    @PostMapping("/update")
    public String updatePlaylist(
            @ModelAttribute Playlist playlist,
            Principal principal,
            @RequestParam("playlistID") Long playlistId) {

        // Get current user
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get existing playlist
        Playlist existing = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        // Verify ownership
        if (!existing.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not allowed to update this playlist.");
        }

        // Update fields
        existing.setPlaylistName(playlist.getPlaylistName());
        existing.setPlaylistDes(playlist.getPlaylistDes());

        // Save changes
        playlistRepository.save(existing);
        return "redirect:/playlist/detail/" + playlistId;
    }

    @GetMapping("/delete/{id}")
    public String deletePlaylist(@PathVariable Long id, Principal principal) throws PlaylistNotFoundException {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

        String username = principal.getName();
        if (!playlist.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not allowed to delete this playlist.");
        }

        playlistRepository.delete(playlist);
        return "redirect:/playlist/list";
    }

}
