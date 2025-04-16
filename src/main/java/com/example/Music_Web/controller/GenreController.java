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
import org.springframework.web.multipart.MultipartFile;
import com.example.Music_Web.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Autowired
	FileStorageService fileStorageService;

	// For admin page
	@GetMapping("/list")
	public String listGenres(Model model) {

		// Dữ liệu cho tab aritst
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);
		Map<Long, Integer> artistTotalPlays = new HashMap<>();
		Map<Long, Integer> artistSongCount = new HashMap<>();
		for (Artist artist : artists) {
			List<Song> songsOfArtist = songRepository.findByArtistsOfSong_ArtistID(artist.getArtistID());
			int totalPlays = 0;
			for (Song song : songsOfArtist) {
				totalPlays += song.getPlays();
			}
			artistTotalPlays.put(artist.getArtistID(), totalPlays);
			artistSongCount.put(artist.getArtistID(), songsOfArtist.size());
		}

		model.addAttribute("artistTotalPlays", artistTotalPlays);
		model.addAttribute("artistSongCount", artistSongCount);
		// Dữ liệu cho tab song
		List<Song> songs = songRepository.findAll();
		model.addAttribute("songs", songs);

		// Dữ liệu cho tab album
		List<Album> albums = albumRepository.findAll();
		model.addAttribute("albums", albums);

		// Dữ liệu của tab genre
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);
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

		List<Song> songsOfGenre = new ArrayList<>(genre.getSongsOfGenre());
		model.addAttribute("songsOfGenre", songsOfGenre);

		return "pages/userPage/genresDetail";
	}

	@GetMapping("/add")
	public String addGenre(Model model) {
		model.addAttribute("genre", new Genre());
		return "pages/adminPage/addGenre";
	}

	@PostMapping("/add")
	public String addGenre(@ModelAttribute Genre genre, @RequestParam("imageFile") MultipartFile imageFile)
			throws IOException {

		// Validate image file
		if (imageFile.isEmpty()) {
			throw new RuntimeException("Please select an image file");
		}

		if (!imageFile.getContentType().startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed for cover");
		}
		// save file
		String imagePath = fileStorageService.storeFile(imageFile);

		genre.setGenreImage(imagePath);
		genreRepository.save(genre);
		return "redirect:/genre/list";
	}

	@GetMapping("/update/{id}")
	public String updateGenre(@PathVariable Long id, Model model)
			throws IOException {

		// get existing genre
		Genre genre = genreRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid genre Id:" + id));
		model.addAttribute("genre", genre);
		return "pages/adminPage/editGenre";
	}

	@PostMapping("/update/{id}")
	public String updateGenre(@PathVariable Long id, @ModelAttribute Genre updatedGenre,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
			throws IOException {

		// get existing genre
		Genre existingGenre = genreRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Genre not found"));

		existingGenre.setGenreName(updatedGenre.getGenreName());
		// Validate image file
		if (imageFile.isEmpty()) {
			throw new RuntimeException("Please select an image file");
		}

		if (!imageFile.getContentType().startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed for cover");
		}

		// 1. If there is an old image file, delete it
		if (existingGenre.getGenreImage() != null) {
			Path oldImageFilePath = Paths.get(existingGenre.getGenreImage());
			if (Files.exists(oldImageFilePath)) {
				Files.delete(oldImageFilePath);
			}
		}
		// 2. Upload new image file and save to /uploads/ or any custom path
		String imagePath = fileStorageService.storeFile(imageFile);
		// 3. Set new paths to genre entity
		existingGenre.setGenreImage(imagePath);
		genreRepository.save(existingGenre);
		return "redirect:/genre/list";
	}

	@GetMapping("/delete/{id}")
	public String deleteGenre(@PathVariable Long id) throws GenreNotFoundException {
		Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreNotFoundException("Genre not found"));

		for (Song song : genre.getSongsOfGenre()) {
			song.getGenresOfSong().remove(genre);
		}
		genre.getSongsOfGenre().clear();
		genreRepository.delete(genre);
		return "redirect:/genre/list";
	}
}
