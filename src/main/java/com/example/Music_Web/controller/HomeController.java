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

    // @GetMapping("/topchart")
    // public String getTopchart(Model model) {
    // model.addAttribute("songs", songRepository.findAll());
    // return "pages/userPage/topchart";
    // }

    @GetMapping("/genres/detail")
    public String getGenresDetail(Model model) {
        return "pages/userPage/genresDetail";
    }

    @GetMapping("/artist")
    public String getArtist(Model model) {
        model.addAttribute("artists", artistRepository.findAll());
        return "pages/userPage/artist";
    }

    @GetMapping("/artist/detail")
    public String getArtistDetail() {
        return "pages/userPage/artistDetail";
    }

    @GetMapping("/album/detail")
    public String getAlbumDetail() {
        return "pages/userPage/albumDetail";
    }

    // @GetMapping("/favorite")
    // public String getFavorite() {
    // return "pages/userPage/favorite";
    // }

    @GetMapping("/admin/album/add")
    public String addAlbum() {
        return "pages/adminPage/addAlbum";
    }

    @GetMapping("/admin/album/edit")
    public String editAlbum() {
        return "pages/adminPage/editAlbum";
    }

    // @GetMapping("/admin/userlist")
    // public String getUserList() {
    // return "pages/adminPage/userList";
    // }

    // @GetMapping("/admin/userlist/view")
    // public String getUserListDetail() {
    // return "pages/adminPage/userDetail";
    // }

    @GetMapping("/admin/static")
    public String getStatic() {
        return "pages/adminPage/static";
    }
}
