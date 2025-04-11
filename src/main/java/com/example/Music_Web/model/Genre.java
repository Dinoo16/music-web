package com.example.Music_Web.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres")
public class Genre {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long genreID;

	@Column(nullable = false, length = 255)
	private String genreName;

	private String genreImage;

	// ----------
	// relationship
	// @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
	// CascadeType.MERGE })
	// @JoinTable(name = "genre_song", joinColumns = @JoinColumn(name = "genre_id"),
	// inverseJoinColumns = @JoinColumn(name = "song_id"))
	// private List<Song> songsOfGenre = new ArrayList<>();

	@ManyToMany(mappedBy = "genresOfSong")
	private List<Song> songsOfGenre = new ArrayList<>();

	// getters & setters

	public Long getGenreID() {
		return genreID;
	}

	public void setGenreID(Long genreID) {
		this.genreID = genreID;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	public String getGenreImage() {
		return genreImage;
	}

	public void setGenreImage(String genreImage) {
		this.genreImage = genreImage;
	}

	public List<Song> getSongsOfGenre() {
		return songsOfGenre;
	}

	public void setSongsOfGenre(List<Song> songsOfGenre) {
		this.songsOfGenre = songsOfGenre;
	}
}
