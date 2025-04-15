package com.example.Music_Web.model;

import com.example.Music_Web.dto.UserDto;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String roles; // "ADMIN,USER,MOD"
    private String email;
    private String avatar;

    @ManyToMany
    @JoinTable(name = "user_recently_played", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    @OrderColumn(name = "play_order")
    private List<Song> recentlyPlayedSongs = new ArrayList<>();

    // relationship
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Playlist> playlists;

    // Constructors
    public User() {
    }

    public User(UserDto userDto, PasswordEncoder passwordEncoder) {
        this.username = userDto.getUsername();
        this.password = passwordEncoder.encode(userDto.getPassword());
        this.roles = (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) ? userDto.getRoles() : "USER";
        this.email = userDto.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public List<Song> getRecentlyPlayedSongs() {
        return recentlyPlayedSongs;
    }

    public void setRecentlyPlayedSongs(List<Song> recentlyPlayedSongs) {
        this.recentlyPlayedSongs = recentlyPlayedSongs;
    }
}
