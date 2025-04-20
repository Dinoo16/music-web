package com.example.Music_Web.controller;

import com.example.Music_Web.repository.PlaylistRepository;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Music_Web.model.Playlist;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
// @RequestMapping("/music")
public class HomeController {
    @Autowired
    SongRepository songRepository;
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    UserRepository userRepository;

    // Songcard in home
    @GetMapping("/")
    public String showSongHomePage(Model model, Principal principal) {
        model.addAttribute("artists", artistRepository.findAll());
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Song> recentlyPlayed = user.getRecentlyPlayedSongs();
            model.addAttribute("recentlyPlayed", recentlyPlayed);
        } else {
            model.addAttribute("recentlyPlayed", List.of());
        }
        return "pages/userPage/home";
    }

    // Songcard in discover
    @GetMapping("/discover")
    public String showSongDiscover(Model model) {
        return "pages/userPage/discover";
    }

    @GetMapping("/admin/static")
    public String getStatic() {
        return "pages/adminPage/static";
    }
}
