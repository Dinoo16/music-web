package com.example.Music_Web.controller;

import com.example.Music_Web.model.Playlist;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.PlaylistRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

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
    public List<Song> addGlobalSongs() {
        return songRepository.findAll();
    }



}
