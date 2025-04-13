package com.example.Music_Web.controller;

import com.example.Music_Web.exception.GenreNotFoundException;
import com.example.Music_Web.exception.SongNotFoundException;
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

@Controller
@RequestMapping("/genre")
public class GenreController {

	@Autowired
	GenreRepository genreRepository;
	@Autowired
	SongRepository songRepository;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	AlbumRepository albumRepository;

	// For admin page
	@GetMapping("/list")
	public String listGenres(Model model) {
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);
		// Dữ liệu cho tab aritst
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);
		// Dữ liệu cho tab song
		List<Song> songs = songRepository.findAll();
		model.addAttribute("songs", songs);
		// Dữ liệu cho tab album
		List<Album> albums = albumRepository.findAll();
		model.addAttribute("albums", albums);

		model.addAttribute("activeTab", "genre");
		return "pages/adminPage/upload";
	}

	// For user page (read-only view)
	@GetMapping(value = "/user/list")
	public String getAllGenresUser(Model model) {
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);
		return "pages/userPage/genres";
	}

	// Genre detail for user page (read-only view)
	@GetMapping("/detail/{id}")
	public String getGenreDetail(@PathVariable Long id, Model model) throws GenreNotFoundException {
		Genre genre = genreRepository.findById(id)
				.orElseThrow(() -> new GenreNotFoundException("Invalid genre ID: " + id));
		model.addAttribute("genre", genre);

		List<Song> songs = new ArrayList<>(genre.getSongsOfGenre());
		model.addAttribute("songs", songs);

		return "pages/userPage/genresDetail";
	}

	@GetMapping("/add")
	public String addGenre(Model model) {
		model.addAttribute("genre", new Genre());
		return "pages/adminPage/addGenre";
	}

	@PostMapping("/add")
	public String addGenre(@ModelAttribute Genre genre) {
		genreRepository.save(genre);
		return "redirect:/genre/list";
	}

	@GetMapping("/update/{id}")
	public String updateGenre(@PathVariable Long id, Model model) throws GenreNotFoundException {
		Genre genre = genreRepository.findById(id)
				.orElseThrow(() -> new GenreNotFoundException("Invalid genre ID: " + id));
		model.addAttribute("genre", genre);
		return "pages/adminPage/editGenre";
	}

	@PostMapping("/update/{id}")
	public String updateGenre(@PathVariable Long id, @ModelAttribute Genre updatedGenre) throws GenreNotFoundException {
		Genre existingGenre = genreRepository.findById(id)
				.orElseThrow(() -> new GenreNotFoundException("Genre not found"));

		existingGenre.setGenreName(updatedGenre.getGenreName());
		existingGenre.setGenreImage(updatedGenre.getGenreImage());

		genreRepository.save(existingGenre);
		return "redirect:/genre/list";
	}

	@GetMapping("/delete/{id}")
	public String deleteGenre(@PathVariable Long id) throws GenreNotFoundException {
		Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreNotFoundException("Genre not found"));
		genreRepository.delete(genre);
		return "redirect:/genre/list";
	}
}
