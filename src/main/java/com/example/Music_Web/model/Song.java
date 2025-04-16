package com.example.Music_Web.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "songs")
public class Song {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long songID;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(nullable = false, length = 255)
	private String filePath;

	@Column(length = 255)
	private String coverImage;

	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date releaseDate;

	@Column(nullable = false)
	private int duration;

	private int plays;

	@PrePersist
	protected void onCreate() {
		this.releaseDate = new Date();
	}
	// ------------------
	// relationship

	// artist
	@ManyToMany
	@JoinTable(name = "artist_song", joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
	private List<Artist> artistsOfSong = new ArrayList<>();

	// genre
	@ManyToMany
	@JoinTable(name = "genre_song", joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
	private List<Genre> genresOfSong = new ArrayList<>();
	// album
	@ManyToOne(optional = true)
	@JoinColumn(name = "album_id", nullable = true)
	private Album album;
	// playlist
	@ManyToMany
	@JoinTable(name = "playlist_song", joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "playlist_id"))
	private List<Playlist> playlists = new ArrayList<>();

	// recently played song
	@ManyToMany(mappedBy = "recentlyPlayedSongs")
	private List<User> usersWhoPlayed = new ArrayList<>();

	// getters & setters
	public Long getSongID() {
		return songID;
	}

	public void setSongID(Long songID) {
		this.songID = songID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public List<Artist> getArtistsOfSong() {
		return artistsOfSong;
	}

	public void setArtistsOfSong(List<Artist> artistsOfSong) {
		this.artistsOfSong = artistsOfSong;
	}

	public List<Genre> getGenresOfSong() {
		return genresOfSong;
	}

	public void setGenresOfSong(List<Genre> genresOfSong) {
		this.genresOfSong = genresOfSong;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public List<Playlist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(List<Playlist> playlists) {
		this.playlists = playlists;
	}

	public int getPlays() {
		return plays;
	}

	public void setPlays(int plays) {
		this.plays = plays;
	}

	public List<User> getUsersWhoPlayed() {
		return usersWhoPlayed;
	}

	public void setUsersWhoPlayed(List<User> usersWhoPlayed) {
		this.usersWhoPlayed = usersWhoPlayed;
	}

	@Transient
	public String getFormattedDuration() {
		int minutes = duration / 60;
		int seconds = duration % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}
}
