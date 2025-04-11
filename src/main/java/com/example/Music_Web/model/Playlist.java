package com.example.Music_Web.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistID;

    @Column(nullable = false, length = 255)
    private String playlistName;

    private String playlistDes;

    // ----------
    // relationship
    @ManyToMany(mappedBy = "playlistsOfSong")
    private List<Song> songsOfPlaylist = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // getters & setters
    public Long getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(Long playlistID) {
        this.playlistID = playlistID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistDes() {
        return playlistDes;
    }

    public void setPlaylistDes(String playlistDes) {
        this.playlistDes = playlistDes;
    }

    public List<Song> getSongsOfPlaylist() {
        return songsOfPlaylist;
    }

    public void setSongsOfPlaylist(List<Song> songsOfPlaylist) {
        this.songsOfPlaylist = songsOfPlaylist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
