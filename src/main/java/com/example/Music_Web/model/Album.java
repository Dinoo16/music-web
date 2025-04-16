package com.example.Music_Web.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "albums")
public class Album {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long albumID;

	@Column(nullable = false, length = 255)
	private String albumName;

	@Column
	private LocalDate releaseDate;

	@Column(length = 255)
	private String coverImage;

	// transient
	@Transient
	private List<Long> songIds;

	@Transient
	private List<Long> artistIds;

	// ----------------
	// relationship
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "album_artist", joinColumns = @JoinColumn(name = "album_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
	private List<Artist> artistsOfAlbum = new ArrayList<>();

	@OneToMany(mappedBy = "album", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private List<Song> songsOfAlbum;

	// getters & setters

	public List<Long> getSongIds() {
		return songIds;
	}

	public void setSongIds(List<Long> songIds) {
		this.songIds = songIds;
	}

	public List<Long> getArtistIds() {
		return artistIds;
	}

	public void setArtistIds(List<Long> artistIds) {
		this.artistIds = artistIds;
	}

	public Long getAlbumID() {
		return albumID;
	}

	public void setAlbumID(Long albumID) {
		this.albumID = albumID;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public List<Artist> getArtistsOfAlbum() {
		return artistsOfAlbum;
	}

	public void setArtistsOfAlbum(List<Artist> artistsOfAlbum) {
		this.artistsOfAlbum = artistsOfAlbum;
	}

	public List<Song> getSongsOfAlbum() {
		return songsOfAlbum;
	}

	public void setSongsOfAlbum(List<Song> songsOfAlbum) {
		this.songsOfAlbum = songsOfAlbum;
	}
}
