package com.example.Music_Web.controller;

import com.example.Music_Web.exception.PlaylistNotFoundException;
import com.example.Music_Web.exception.SongNotFoundException;
import com.example.Music_Web.model.Playlist;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.PlaylistRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/playlist/songs")
public class PlaylistSongController {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    @Autowired
    public PlaylistSongController(PlaylistRepository playlistRepository,
            SongRepository songRepository,
            UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public String addSongToPlaylist(
            @RequestParam Long playlistId,
            @RequestParam Long songId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Playlist playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with id: " + playlistId));

            if (!playlist.getUser().equals(user)) {
                throw new SecurityException("You don't own this playlist");
            }

            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new SongNotFoundException("Song not found with id: " + songId));

            if (!playlist.getSongs().contains(song)) {
                playlist.addSong(song);
                playlistRepository.save(playlist);
                redirectAttributes.addFlashAttribute("success", "Song added to playlist!");
            } else {
                redirectAttributes.addFlashAttribute("info", "Song already in playlist");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/playlist/detail/" + playlistId;
    }

    @PostMapping("/{playlistId}/remove-song")
    public ResponseEntity<String> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @RequestParam Long songId,
            Principal principal) {

        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Playlist playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

            if (!playlist.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't own this playlist");
            }

            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new SongNotFoundException("Song not found"));

            playlist.removeSong(song);
            playlistRepository.save(playlist);

            return ResponseEntity.ok("Song removed successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing song: " + e.getMessage());
        }
    }

    @GetMapping("/{playlistId}")
    @ResponseBody
    public List<Song> getPlaylistSongs(@PathVariable Long playlistId) {
        return playlistRepository.findById(playlistId)
                .map(Playlist::getSongs)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));
    }

    @GetMapping("/search")
    public String searchSongsToAdd(
            @RequestParam Long playlistId,
            @RequestParam(required = false) String query,
            Model model,
            Principal principal) {

        try {
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Playlist playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

            if (!playlist.getUser().equals(user)) {
                throw new SecurityException("You don't own this playlist");
            }

            List<Song> songs;
            if (query != null && !query.isEmpty()) {
                songs = songRepository.findByTitleContainingIgnoreCase(query);
                // Filter out songs already in playlist
                songs.removeAll(playlist.getSongs());
            } else {
                // Get all songs not in playlist
                songs = songRepository.findAll();
                songs.removeAll(playlist.getSongs());
            }

            model.addAttribute("playlist", playlist);
            model.addAttribute("songs", songs);
            model.addAttribute("query", query);

        } catch (Exception e) {
            model.addAttribute("error", "Error searching songs: " + e.getMessage());
        }

        return "pages/userPage/addSongsToPlaylist";
    }
}