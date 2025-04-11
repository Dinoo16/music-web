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

	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date releaseDate;

	@Column(nullable = false)
	private int duration;

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
	@ManyToOne
	@JoinColumn(name = "album_id", nullable = false)
	private Album album;
	// playlist
	@ManyToMany
	@JoinTable(name = "playlist_song", joinColumns = @JoinColumn(name = "song_id"), inverseJoinColumns = @JoinColumn(name = "playlist_id"))
	private List<Playlist> playlistsOfSong = new ArrayList<>();

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

	public List<Playlist> getPlaylistsOfSong() {
		return playlistsOfSong;
	}

	public void setPlaylistsOfSong(List<Playlist> playlistsOfSong) {
		this.playlistsOfSong = playlistsOfSong;
	}
}
