package com.example.Music_Web.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artists")
public class Artist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long artistID;

	@Column(nullable = false, length = 255)
	private String artistName;

	@Column(columnDefinition = "TEXT")
	private String bio;

	@Column(length = 255)
	private String image;

	// -----------
	// relationship
	@ManyToMany(mappedBy = "artistsOfSong", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<Song> songsOfArtist;

	@ManyToMany(mappedBy = "artistsOfAlbum", cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REMOVE
	})
	private List<Album> albumsOfArtist = new ArrayList<>();

	// getters & setters
	public Long getArtistID() {
		return artistID;
	}

	public void setArtistID(Long artistID) {
		this.artistID = artistID;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Song> getSongsOfArtist() {
		return songsOfArtist;
	}

	public void setSongsOfArtist(List<Song> songsOfArtist) {
		this.songsOfArtist = songsOfArtist;
	}

	public List<Album> getAlbumsOfArtist() {
		return albumsOfArtist;
	}

	public void setAlbumsOfArtist(List<Album> albumsOfArtist) {
		this.albumsOfArtist = albumsOfArtist;
	}
}
