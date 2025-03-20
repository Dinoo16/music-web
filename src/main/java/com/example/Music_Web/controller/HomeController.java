package com.example.Music_Web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {

        return "pages/home";
    }

    @GetMapping("/signin")
    public String signin() {
        return "pages/signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "pages/signup";
    }

    @GetMapping("/song")
    public String getSong() {
        return "pages/song";
    }

    @GetMapping("/artist")
    public String getArtist() {
        return "pages/artist";
    }

    @GetMapping("/artist/detail")
    public String getArtistDetail() {
        return "pages/artistDetail";
    }

    @GetMapping("/album")
    public String getAlbum() {
        return "pages/album";
    }

    @GetMapping("/album/detail")
    public String getAlbumDetail() {
        return "pages/albumDetail";
    }

    @GetMapping("/myplaylist")
    public String getMyPlaylist() {
        return "pages/myplaylist";
    }

    @GetMapping("/myplaylist/detail")
    public String getMyPlaylistDetail() {
        return "pages/myplaylistDetail";
    }

    @GetMapping("/song/detail")
    public String getSongDetail() {
        return "pages/songDetail";
    }
}
