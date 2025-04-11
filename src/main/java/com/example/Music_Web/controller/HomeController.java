package com.example.Music_Web.controller;

import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.Music_Web.model.User;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String getHome(Model model) {
        return "pages/userPage/home";
    }

    @GetMapping("/discover")
    public String getDiscover(Model model) {
        return "pages/userPage/discover";
    }

    @GetMapping("/topchart")
    public String getTopchart(Model model) {
        return "pages/userPage/topchart";
    }

    @GetMapping("/genres")
    public String getGenres(Model model) {
        return "pages/userPage/genres";
    }

    @GetMapping("/genres/detail")
    public String getGenresDetail(Model model) {
        return "pages/userPage/genresDetail";
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

    // @GetMapping("/myplaylist")
    // public String getMyPlaylist() {
    // return "pages/userPage/myplaylist";
    // }

    // @GetMapping("/myplaylist/detail")
    // public String getMyPlaylistDetail() {
    //     return "pages/userPage/myplaylistDetail";
    // }

    @GetMapping("/song/detail")
    public String getSongDetail() {
        return "pages/userPage/songDetail";
    }

    @GetMapping("/favorite")
    public String getFavorite() {
        return "pages/userPage/favorite";
    }

    @GetMapping("/setting")
    public String showSettingPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElse(null); // hoặc throw nếu bạn muốn xử lý lỗi

        if (user == null) {
            return "redirect:/auth/signin?error=user-not-found";
        }

        model.addAttribute("user", user);
        return "pages/userPage/setting";
    }

    @GetMapping("/admin/album/add")
    public String addAlbum() {
        return "pages/adminPage/addAlbum";
    }

    @GetMapping("/admin/album/edit")
    public String editAlbum() {
        return "pages/adminPage/editAlbum";
    }

    @GetMapping("/admin/userlist")
    public String getUserList() {
        return "pages/adminPage/userList";
    }

    @GetMapping("/admin/userlist/view")
    public String getUserListDetail() {
        return "pages/adminPage/userDetail";
    }

    @GetMapping("/admin/comment")
    public String getUserComment() {
        return "pages/adminPage/userComment";
    }

    @GetMapping("/admin/playlist")
    public String getUserPlaylist() {
        return "pages/adminPage/playlist";
    }

    @GetMapping("/admin/playlist/edit")
    public String getPlaylistEdit() {
        return "pages/adminPage/editPlaylist";
    }

    @GetMapping("/admin/static")
    public String getStatic() {
        return "pages/adminPage/static";
    }
}
