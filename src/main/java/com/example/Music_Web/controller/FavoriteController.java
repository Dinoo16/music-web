package com.example.Music_Web.controller;

import com.example.Music_Web.model.Favorite;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.FavoriteRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    // Trang danh sách bài hát yêu thích
    @GetMapping("/favorite")
    public String getFavorite(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/signin";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        List<Song> favoriteSongs = new ArrayList<>();

        for (Favorite favorite : favorites) {
            favoriteSongs.add(favorite.getSong());
        }

        model.addAttribute("songs", favoriteSongs);
        return "pages/userPage/favorite";
    }

    // API toggle trạng thái yêu thích của 1 bài hát
    @PostMapping("/api/favorites/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Long> payload, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        Long songID = payload.get("songID");
        if (songID == null) {
            return ResponseEntity.badRequest().body("Song ID is required");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Song> songOpt = songRepository.findById(songID);
        if (songOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Song not found");
        }

        Song song = songOpt.get();
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndSong(user, song);

        boolean isFavorite;
        if (existingFavorite.isPresent()) {
            // Bỏ yêu thích
            favoriteRepository.delete(existingFavorite.get());
            isFavorite = false;
        } else {
            // Thêm vào yêu thích
            Favorite favorite = new Favorite(user, song);
            favoriteRepository.save(favorite);
            isFavorite = true;
        }

        return ResponseEntity.ok().body(Map.of("isFavorite", isFavorite));
    }

    // API kiểm tra 1 bài hát có đang được yêu thích không
    @GetMapping("/api/favorites/check/{songID}")
    @ResponseBody
    public ResponseEntity<?> checkFavorite(@PathVariable Long songID, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok().body(Map.of("isFavorite", false));
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Song> songOpt = songRepository.findById(songID);
        if (songOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Song not found");
        }

        Song song = songOpt.get();
        boolean isFavorite = favoriteRepository.existsByUserAndSong(user, song);

        return ResponseEntity.ok().body(Map.of("isFavorite", isFavorite));
    }
}
