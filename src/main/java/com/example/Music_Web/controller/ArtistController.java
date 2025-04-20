package com.example.Music_Web.controller;

import com.example.Music_Web.exception.AlbumNotFoundException;
import com.example.Music_Web.exception.ArtistNotFoundException;
import com.example.Music_Web.exception.GenreNotFoundException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.Music_Web.service.FileStorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//1.	Create Artist
//2.	Get All Artists
//4.	Update Artist
//5.	Delete Artist
@Controller
public class ArtistController {
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

	// for admin
	@GetMapping(value = "/admin/artist/list")
	public String getAllArtists(Model model) {

		// Dữ liệu cho tab song
		List<Song> songs = songRepository.findAll();
		model.addAttribute("songs", songs);
		// Dữ liệu cho tab genre
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);

		// Dữ liệu cho tab album
		List<Album> albums = albumRepository.findAll();
		Map<Long, String> albumArtistNames = new HashMap<>();
		for (Album album : albums) {
			// Get all artists for this album (both direct album artists and song artists)
			Set<String> artistNames = new LinkedHashSet<>();

			// Add direct album artists
			album.getArtistsOfAlbum().stream()
					.map(Artist::getArtistName)
					.forEach(artistNames::add);

			// Add artists from songs in the album
			album.getSongsOfAlbum().stream()
					.flatMap(song -> song.getArtistsOfSong().stream())
					.map(Artist::getArtistName)
					.forEach(artistNames::add);

			// Join all artist names with commas
			albumArtistNames.put(album.getAlbumID(), String.join(", ", artistNames));
		}
		model.addAttribute("albumArtistNames", albumArtistNames);
		model.addAttribute("albums", albums);

		// Dữ liệu của tab artist
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);
		// Sum of plays and songs for each artist

		model.addAttribute("activeTab", "artist");
		return "pages/adminPage/upload";

	}

	// For user page (read-only view)
	@GetMapping(value = "/artist/list")
	public String getAllArtistsUser(Model model) {
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);

		return "pages/userPage/artist";
	}

	// Artist detail for user page (read-only view)
	@GetMapping("/artist/detail/{id}")
	public String getArtistDetail(@PathVariable Long id, Model model) throws ArtistNotFoundException {
		Artist artist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Invalid artist ID: " + id));

		// Get direct albums (where artist is in artistsOfAlbum)
		List<Album> directAlbums = albumRepository.findByArtistsOfAlbum_ArtistID(id);

		// Get albums through songs (where artist has songs in album)
		List<Album> albumsThroughSongs = albumRepository.findDistinctBySongsOfAlbum_ArtistsOfSong_ArtistID(id);

		// Combine and deduplicate albums
		Set<Album> allAlbums = new LinkedHashSet<>();
		allAlbums.addAll(directAlbums);
		allAlbums.addAll(albumsThroughSongs);

		// Create a map of album ID to artist names for display
		Map<Long, String> albumArtists = new HashMap<>();
		for (Album album : allAlbums) {
			// Get all artists associated with this album (both direct and through songs)
			Set<Artist> artistsForAlbum = new LinkedHashSet<>();
			artistsForAlbum.addAll(album.getArtistsOfAlbum());

			// Add artists from songs in the album
			if (album.getSongsOfAlbum() != null) {
				album.getSongsOfAlbum().stream()
						.flatMap(song -> song.getArtistsOfSong().stream())
						.forEach(artistsForAlbum::add);
			}

			// Convert to comma-separated string
			String artistNames = artistsForAlbum.stream()
					.map(Artist::getArtistName)
					.collect(Collectors.joining(", "));

			albumArtists.put(album.getAlbumID(), artistNames);
		}

		// Get all songs by this artist
		List<Song> songsOfArtist = songRepository.findByArtistsOfSong_ArtistID(id);

		// Sum of song plays
		int totalPlays = songsOfArtist.stream().mapToInt(Song::getPlays).sum();

		model.addAttribute("artist", artist);
		model.addAttribute("albumArtists", albumArtists);
		model.addAttribute("totalPlays", totalPlays);
		model.addAttribute("songsOfArtist", songsOfArtist);
		model.addAttribute("albums", new ArrayList<>(allAlbums));

		return "pages/userPage/artistDetail";
	}

	// for admin
	@GetMapping(value = "/admin/artist/update/{id}")
	public String updateArtist(
			@PathVariable(value = "id") Long id, Model model) throws ArtistNotFoundException {
		Artist artist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Artist not found"));
		List<Album> albums = new ArrayList<>(artist.getAlbumsOfArtist());
		List<Song> songs = new ArrayList<>(artist.getSongsOfArtist());

		artist.setAlbumsOfArtist(albums);
		artist.setSongsOfArtist(songs);

		model.addAttribute("artist", artist);
		model.addAttribute("albums", albums);
		model.addAttribute("songs", songs);
		return "pages/adminPage/editArtist";
	}

	@PostMapping("/admin/artist/update/{id}")
	public String updateArtist(@PathVariable Long id,
			@ModelAttribute Artist updatedArtist,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
			throws IOException, ArtistNotFoundException {

		Artist existingArtist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Artist not found"));

		// Update text fields
		existingArtist.setArtistName(updatedArtist.getArtistName());
		existingArtist.setBio(updatedArtist.getBio());
		existingArtist.setSongsOfArtist(updatedArtist.getSongsOfArtist());
		existingArtist.setAlbumsOfArtist(updatedArtist.getAlbumsOfArtist());

		// Update image if new one is uploaded
		if (imageFile != null && !imageFile.isEmpty()) {
			if (!imageFile.getContentType().startsWith("image/")) {
				throw new RuntimeException("Only image files are allowed.");
			}

			// Optional: Delete old image if needed
			// Path oldPath = Paths.get(existingArtist.getImage());
			// Files.deleteIfExists(oldPath);

			String imagePath = fileStorageService.storeFile(imageFile);
			existingArtist.setImage(imagePath);
		}

		artistRepository.save(existingArtist);
		return "redirect:/admin/artist/list";
	}

	@GetMapping(value = "/admin/artist/add")
	public String addArtist(Model model) {

		Artist artist = new Artist();
		List<Song> songs = songRepository.findAll();
		List<Album> albums = albumRepository.findAll();

		model.addAttribute("songs", songs);
		model.addAttribute("albums", albums);
		model.addAttribute("artist", artist);
		return "pages/adminPage/addArtist";
	}

	@PostMapping(value = "/admin/artist/add")
	public String addArtist(@ModelAttribute Artist artist,
			@RequestParam("imageFile") MultipartFile imageFile,
			RedirectAttributes redirectAttributes) throws IOException {

		// Validate image
		if (imageFile.isEmpty()) {
			throw new RuntimeException("Please select an image file.");
		}

		if (!imageFile.getContentType().startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed.");
		}

		// Save image
		String imagePath = fileStorageService.storeFile(imageFile);
		artist.setImage(imagePath);

		artistRepository.save(artist);
		redirectAttributes.addFlashAttribute("activeTab", "artist");

		return "redirect:/admin/artist/list";
	}

	@GetMapping(value = "/admin/artist/delete/{id}")
	public String deleteArtist(@PathVariable(value = "id") Long id) throws ArtistNotFoundException {
		Artist artist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Artist not found"));

		// 1. Remove artist from all songs
		for (Song song : artist.getSongsOfArtist()) {
			song.getArtistsOfSong().remove(artist);
			songRepository.save(song);
		}

		// 2. Remove artist from all albums
		for (Album album : artist.getAlbumsOfArtist()) {
			album.getArtistsOfAlbum().remove(artist);
			albumRepository.save(album);
		}

		// 3. Clear collections to avoid Hibernate issues
		artist.getSongsOfArtist().clear();
		artist.getAlbumsOfArtist().clear();
		artistRepository.save(artist);

		// 4. Finally delete the artist
		artistRepository.delete(artist);

		return "redirect:/admin/artist/list";
	}
}
