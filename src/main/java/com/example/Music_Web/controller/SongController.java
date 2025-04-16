package com.example.Music_Web.controller;

import com.example.Music_Web.exception.AlbumNotFoundException;
import com.example.Music_Web.exception.PlaylistNotFoundException;
import com.example.Music_Web.exception.SongNotFoundException;
import com.example.Music_Web.model.Album;
import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Genre;
import com.example.Music_Web.model.Playlist;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.UserRepository;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.SongRepository;
import com.example.Music_Web.service.FileStorageService;
import org.apache.commons.lang3.time.DurationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//1.	Create Song (Upload)
//2.	Get All Songs
//6.	Update Song
//7.	Delete Song

@Controller
@RequestMapping("/song")
public class SongController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	SongRepository songRepository;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	AlbumRepository albumRepository;
	@Autowired
	FileStorageService fileStorageService;

	// For admin page (with all controls)
	// @GetMapping(value = "/admin/list")
	// public String getAllSongs(Model model) {

	// // Dữ liệu cho tab artist
	// List<Artist> artists = artistRepository.findAll();
	// model.addAttribute("artists", artists);
	// Map<Long, Integer> artistTotalPlays = new HashMap<>();
	// Map<Long, Integer> artistSongCount = new HashMap<>();
	// for (Artist artist : artists) {
	// List<Song> songsOfArtist =
	// songRepository.findByArtistsOfSong_ArtistID(artist.getArtistID());
	// int totalPlays = 0;
	// for (Song song : songsOfArtist) {
	// totalPlays += song.getPlays();
	// }
	// artistTotalPlays.put(artist.getArtistID(), totalPlays);
	// artistSongCount.put(artist.getArtistID(), songsOfArtist.size());
	// }
	// model.addAttribute("artistTotalPlays", artistTotalPlays);
	// model.addAttribute("artistSongCount", artistSongCount);

	// // Dữ liệu cho tab genre
	// List<Genre> genres = genreRepository.findAll();
	// model.addAttribute("genres", genres);
	// Map<Long, Integer> genreSongCount = new HashMap<>();
	// for (Genre genre : genres) {
	// List<Song> songsOfGenre =
	// songRepository.findByGenresOfSong_GenreID(genre.getGenreID());
	// genreSongCount.put(genre.getGenreID(), songsOfGenre.size());
	// }
	// model.addAttribute("genreSongCount", genreSongCount);
	// // Dữ liệu cho tab album
	// List<Album> albums = albumRepository.findAll();
	// model.addAttribute("albums", albums);

	// // Dữ liệu của tab song
	// List<Song> songs = songRepository.findAll();
	// model.addAttribute("songs", songs);
	// model.addAttribute("activeTab", "song");
	// return "pages/adminPage/upload";
	// }

	// For user page (read-only view)
	@GetMapping(value = "/user/list")
	public String getAllSongsUser(Model model) {
		List<Song> songs = songRepository.findAll();
		// model.addAttribute("songs", songs);
		// genres for filtering

		model.addAttribute("songs", songs);
		model.addAttribute("genres", genreRepository.findAll());
		return "pages/userPage/song";
	}

	// Song detail for user page (read-only view)
	@GetMapping("/detail/{id}")
	public String getSongDetail(@PathVariable Long id, Model model) throws SongNotFoundException {
		Song song = songRepository.findById(id)
				.orElseThrow(() -> new SongNotFoundException("Invalid song ID: " + id));
		model.addAttribute("song", song);
		return "pages/userPage/songDetail";
	}

	@GetMapping("/update/{id}")
	public String getUpdateForm(@PathVariable("id") Long id, Model model) {
		Song song = songRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid song Id:" + id));

		// Get all available options
		List<Album> albumList = albumRepository.findAll();
		List<Genre> genres = genreRepository.findAll();
		List<Artist> artists = artistRepository.findAll();

		// Get currently selected IDs
		List<Long> selectedGenreIds = song.getGenresOfSong().stream()
				.map(Genre::getGenreID)
				.collect(Collectors.toList());

		List<Long> selectedArtistIds = song.getArtistsOfSong().stream()
				.map(Artist::getArtistID)
				.collect(Collectors.toList());

		// Add default album if album is null
		Long selectedAlbumId = (song.getAlbum() != null) ? song.getAlbum().getAlbumID() : null;

		model.addAttribute("song", song);
		model.addAttribute("albumList", albumList);
		model.addAttribute("genres", genres);
		model.addAttribute("artists", artists);
		model.addAttribute("selectedGenreIds", selectedGenreIds);
		model.addAttribute("selectedArtistIds", selectedArtistIds);
		model.addAttribute("selectedAlbumId", selectedAlbumId);
		return "pages/adminPage/editSonglist";
	}

	@PostMapping("/update/{id}")
	public String saveUpdate(
			@PathVariable("id") Long id,
			@ModelAttribute Song song,
			@RequestParam("selectedGenres") String genreIds,
			@RequestParam("selectedArtists") String artistIds,
			@RequestParam(value = "audioFile", required = false) MultipartFile audioFile,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

		// Convert comma-separated IDs to lists
		List<Long> genreIdList = Arrays.stream(genreIds.split(","))
				.filter(genreId -> !genreId.isEmpty())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		List<Long> artistIdList = Arrays.stream(artistIds.split(","))
				.filter(artistId -> !artistId.isEmpty())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		// Get existing song
		Song existingSong = songRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid song Id:" + id));

		// Update fields (like song title)
		existingSong.setTitle(song.getTitle());

		// Check if the audio file is provided (if not, keep the old file path)
		if (audioFile != null && !audioFile.isEmpty()) {
			// Validate audio file
			if (!audioFile.getContentType().startsWith("audio/")) {
				throw new RuntimeException("Only audio files are allowed");
			}

			// If there is an old audio file, delete it
			if (existingSong.getFilePath() != null) {
				Path oldAudioFilePath = Paths.get(existingSong.getFilePath());
				if (Files.exists(oldAudioFilePath)) {
					Files.delete(oldAudioFilePath); // Delete old audio file
				}
			}

			// Upload new audio file
			String audioPath = fileStorageService.storeFile(audioFile);
			existingSong.setFilePath(audioPath);
		}

		// Check if the image file is provided (if not, keep the old image path)
		if (imageFile != null && !imageFile.isEmpty()) {
			// Validate image file
			if (!imageFile.getContentType().startsWith("image/")) {
				throw new RuntimeException("Only image files are allowed for cover");
			}

			// If there is an old image file, delete it
			if (existingSong.getCoverImage() != null) {
				Path oldImageFilePath = Paths.get(existingSong.getCoverImage());
				if (Files.exists(oldImageFilePath)) {
					Files.delete(oldImageFilePath); // Delete old image file
				}
			}

			// Upload new image file
			String imagePath = fileStorageService.storeFile(imageFile);
			existingSong.setCoverImage(imagePath);
		}

		// Update relationships only if new genres or artists are provided
		if (!genreIdList.isEmpty()) {
			List<Genre> genres = genreRepository.findAllById(genreIdList);
			existingSong.setGenresOfSong(genres);
		}

		if (!artistIdList.isEmpty()) {
			List<Artist> artists = artistRepository.findAllById(artistIdList);
			existingSong.setArtistsOfSong(artists);
		}

		// Update album only if it's not null
		if (song.getAlbum() != null && song.getAlbum().getAlbumID() != null) {
			Album album = albumRepository.findById(song.getAlbum().getAlbumID())
					.orElseThrow(() -> new IllegalArgumentException("Invalid album Id"));
			existingSong.setAlbum(album);
		} else {
			// No album selected, so keep it as null
			existingSong.setAlbum(null);
		}

		// Save the updated song
		songRepository.save(existingSong);

		return "redirect:/song/list";
	}

	@GetMapping(value = "/add")
	public String addSong(Model model) {
		Song song = new Song();
		List<Artist> artists = artistRepository.findAll();
		List<Album> albumList = albumRepository.findAll();
		List<Genre> genres = genreRepository.findAll();

		model.addAttribute("song", song);
		model.addAttribute("artists", artists);
		model.addAttribute("albumList", albumList);
		model.addAttribute("genres", genres);
		return "pages/adminPage/addSong";
	}

	@PostMapping("/add")
	public String saveSong(
			@ModelAttribute("song") Song song,
			@RequestParam("selectedGenres") String genreIds,
			@RequestParam("selectedArtists") String artistIds,
			@RequestParam("audioFile") MultipartFile audioFile,
			@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
		// Validate audio file
		if (audioFile.isEmpty()) {
			throw new RuntimeException("Please select an audio file");
		}

		if (!audioFile.getContentType().startsWith("audio/")) {
			throw new RuntimeException("Only audio files are allowed");
		}

		// Validate image file
		if (imageFile.isEmpty()) {
			throw new RuntimeException("Please select an image file");
		}

		if (!imageFile.getContentType().startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed for cover");
		}

		// 1. Upload file and save to /uploads/
		String audioPath = fileStorageService.storeFile(audioFile);
		String imagePath = fileStorageService.storeFile(imageFile);
		// 2. Extract duration
		try {
			int duration = fileStorageService.getDurationFromFile(audioFile);
			song.setDuration(duration);
		} catch (Exception e) {
			e.printStackTrace();
			song.setDuration(0);
		}

		// 3. Set path to song entity
		song.setFilePath(audioPath);
		song.setCoverImage(imagePath);

		// Convert comma-separated IDs to lists of Long
		List<Long> genreIdList = Arrays.stream(genreIds.split(","))
				.filter(id -> !id.isEmpty())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		List<Long> artistIdList = Arrays.stream(artistIds.split(","))
				.filter(id -> !id.isEmpty())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		// Fetch genres and artists from repository
		List<Genre> selectedGenres = genreRepository.findAllById(genreIdList);
		List<Artist> selectedArtists = artistRepository.findAllById(artistIdList);

		// Set the relationships
		song.setGenresOfSong(selectedGenres);
		song.setArtistsOfSong(selectedArtists);

		// Save the song
		songRepository.save(song);

		// Update the inverse side of the relationship for genres
		for (Genre genre : selectedGenres) {
			genre.getSongsOfGenre().add(song);
			genreRepository.save(genre);
		}

		return "redirect:/song/list";
	}

	@GetMapping(value = "/delete/{id}")
	public String deleteSong(@PathVariable(value = "id") Long id) throws SongNotFoundException {
		Song song = songRepository.findById(id)
				.orElseThrow(() -> new SongNotFoundException("Song not found"));
		for (User user : song.getUsersWhoPlayed()) {
			user.getRecentlyPlayedSongs().remove(song);
		}
		song.getUsersWhoPlayed().clear();
		songRepository.delete(song);
		return "redirect:/song/list";
	}

	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		try {
			Path file = Paths.get("uploads").resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType("audio/mpeg"))
						.body(resource);
			} else {
				throw new RuntimeException("Could not read file: " + filename);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@PostMapping("/play/{id}")
	public ResponseEntity<String> playSong(@PathVariable Long id) {
		Song song = songRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Song not found"));

		song.setPlays(song.getPlays() + 1);
		songRepository.save(song);
		return ResponseEntity.ok("Play count updated");
	}

	@GetMapping("/topchart")
	public String getTopCharts(Model model) {
		List<Song> songs = songRepository.findAll(Sort.by(Sort.Direction.DESC, "plays"));
		model.addAttribute("songs", songs);

		return "pages/userPage/topchart";
	}
}
