package com.example.Music_Web.controller;

import com.example.Music_Web.exception.AlbumNotFoundException;
import com.example.Music_Web.model.Album;
import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Genre;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

//1.	Create Album
//2.	Get All Albums
//5.	Update Album
//6.	Delete Album

@Controller
@RequestMapping(value = "/album")
public class AlbumController {
	@Autowired
	SongRepository songRepository;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	AlbumRepository albumRepository;

	@GetMapping(value = "/list")
	public String getAllAlbums(Model model) {
		List<Album> albums = albumRepository.findAll();
		model.addAttribute("albums", albums);

		// Dữ liệu cho tab artist
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);
		// Dữ liệu cho tab song
		List<Song> songs = songRepository.findAll();
		model.addAttribute("songs", songs);
		// Dữ liệu cho tab genre
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);

		return "albumList";
	}

	@GetMapping(value = "/update/{id}")
	public String updateAlbum(
			@PathVariable(value = "id") Long id, Model model) throws AlbumNotFoundException {
		Album album = albumRepository.findById(id)
				.orElseThrow(() -> new AlbumNotFoundException("Album not found"));
		List<Song> songs = new ArrayList<>(album.getSongsOfAlbum());
		List<Artist> artists = new ArrayList<>(album.getArtistsOfAlbum());

		album.setSongsOfAlbum(songs);
		album.setArtistsOfAlbum(artists);

		model.addAttribute("songs", songs);
		model.addAttribute("artists", artists);
		model.addAttribute("album", album);
		return "albumUpdate";
	}

	// ---------------------------------
	@PostMapping("/update")
	public String UpdatedAlbum(Album album) {
		// Convert songIds and artistIds back to full objects
		List<Song> selectedSongs = songRepository.findAllById(album.getSongIds());
		List<Artist> selectedArtists = artistRepository.findAllById(album.getArtistIds());

		for (Song song : selectedSongs) {
			song.setAlbum(album); // important for OneToMany
		}

		album.setSongsOfAlbum(selectedSongs);
		album.setArtistsOfAlbum(selectedArtists);

		albumRepository.save(album);
		return "redirect:/album/list";
	}

	@GetMapping(value = "/add")
	public String addAlbum(Model model) {
		Album album = new Album();
		List<Song> songs = songRepository.findAll();
		List<Artist> artists = artistRepository.findAll();

		model.addAttribute("songs", songs);
		model.addAttribute("artists", artists);
		model.addAttribute("album", album);
		return "pages/adminPage/addAlbum";
	}

	@PostMapping(value = "/add")
	public String addAlbum(Album album) {
		System.out.println(">>> Album name: " + album.getAlbumName());
		albumRepository.save(album);
		return "redirect:/album/list";
	}

	@GetMapping(value = "/delete/{id}")
	public String deleteAlbum(@PathVariable(value = "id") Long id) throws AlbumNotFoundException {
		Album album = albumRepository.findById(id)
				.orElseThrow(() -> new AlbumNotFoundException("Album not found"));
		albumRepository.delete(album);
		return "redirect:/album/list";
	}
	//
}
