package com.example.Music_Web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {

        return "pages/userPage/home";
    }

    @GetMapping("/signin")
    public String signin() {
        return "pages/userPage/signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "pages/userPage/signup";
    }

    @GetMapping("/song")
    public String getSong() {
        return "pages/userPage/song";
    }

    @GetMapping("/artist")
    public String getArtist() {
        return "pages/userPage/artist";
    }

    @GetMapping("/artist/detail")
    public String getArtistDetail() {
        return "pages/userPage/artistDetail";
    }

    @GetMapping("/album")
    public String getAlbum() {
        return "pages/userPage/album";
    }

    @GetMapping("/album/detail")
    public String getAlbumDetail() {
        return "pages/userPage/albumDetail";
    }

    @GetMapping("/myplaylist")
    public String getMyPlaylist() {
        return "pages/userPage/myplaylist";
    }

    @GetMapping("/myplaylist/detail")
    public String getMyPlaylistDetail() {
        return "pages/userPage/myplaylistDetail";
    }

    @GetMapping("/song/detail")
    public String getSongDetail() {
        return "pages/userPage/songDetail";
    }

    @GetMapping("/favorite")
    public String getFavorite() {
        return "pages/userPage/favorite";
    }

    @GetMapping("/setting")
    public String getSetting() {
        return "pages/userPage/setting";
    }

    @GetMapping("/admin/songlist")
    public String getSongList() {
        return "pages/adminPage/songList";
    }

    @GetMapping("/admin/userlist")
    public String getUserList() {
        return "pages/adminPage/userList";
    }

    @GetMapping("/admin/comment")
    public String getUserComment() {
        return "pages/adminPage/userComment";
    }

    @GetMapping("/admin/playlist")
    public String getUserPlaylist() {
        return "pages/adminPage/playlist";
    }

}
