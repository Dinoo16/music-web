package com.example.Music_Web.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

	public void addSong(Song song) {
		songsOfAlbum.add(song);
		song.setAlbum(this);
	}

	public void removeSong(Song song) {
		songsOfAlbum.remove(song);
		song.setAlbum(null);
	}

	@Transient
	public List<Artist> getAllArtistsFromSongs() {
		if (songsOfAlbum == null) {
			return new ArrayList<>();
		}
		return songsOfAlbum.stream()
				.flatMap(song -> song.getArtistsOfSong().stream())
				.distinct()
				.collect(Collectors.toList());
	}
}
