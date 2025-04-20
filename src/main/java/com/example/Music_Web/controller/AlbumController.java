package com.example.Music_Web.controller;

import com.example.Music_Web.exception.AlbumNotFoundException;
import com.example.Music_Web.model.Album;
import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Genre;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.service.FileStorageService;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.GenreRepository;
import com.example.Music_Web.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

//1.	Create Album
//2.	Get All Albums
//5.	Update Album
//6.	Delete Album

@Controller
public class AlbumController {
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

	@GetMapping(value = "/admin/album/list")
	public String getAllAlbums(Model model) throws AlbumNotFoundException {

		List<Album> albums = albumRepository.findAll();
		// Create a map of album ID to artist names for ALL albums
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

		// Dữ liệu của tab artist
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);

		// Dữ liệu cho tab song
		List<Song> songs = songRepository.findAll();
		model.addAttribute("songs", songs);
		// Dữ liệu cho tab genre
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);

		// Dữ liệu cho tab album			
		model.addAttribute("albumArtistNames", albumArtistNames);
		model.addAttribute("albums", albums);
		model.addAttribute("activeTab", "album");
		
		return "pages/adminPage/upload";
	}

	// For user page (read-only view)
	@GetMapping(value = "/album/list")

	public String getAllAlbumsUser(Model model) {
		List<Album> albums = albumRepository.findAll();
		// Build map of album ID -> artist name(s)
		Map<Long, String> albumArtists = new HashMap<>();
		for (Album album : albums) {
			String artistNames = album.getArtistsOfAlbum().stream()
					.map(Artist::getArtistName)
					.collect(Collectors.joining(", "));
			albumArtists.put(album.getAlbumID(), artistNames);
		}
		model.addAttribute("albumArtists", albumArtists);
		model.addAttribute("albums", albums);
		return "pages/userPage/album";
	}

	@GetMapping("/album/detail/{id}")
	public String getAlbumDetail(@PathVariable("id") Long id, Model model) throws AlbumNotFoundException {
		Album album = albumRepository.findById(id)
				.orElseThrow(() -> new AlbumNotFoundException("Album not found"));

		// Combine and deduplicate artists
		Set<Artist> allArtists = new LinkedHashSet<>();

		// Add album artists
		allArtists.addAll(album.getArtistsOfAlbum());

		// Add song artists
		album.getSongsOfAlbum().stream()
				.flatMap(song -> song.getArtistsOfSong().stream())
				.forEach(allArtists::add);

		// Get artist names as string
		String combinedArtistNames = allArtists.stream()
				.map(Artist::getArtistName)
				.collect(Collectors.joining(", "));

		model.addAttribute("allArtists", new ArrayList<>(allArtists));
		model.addAttribute("combinedArtistNames", combinedArtistNames);
		model.addAttribute("album", album);
		model.addAttribute("songs", album.getSongsOfAlbum());

		return "pages/userPage/albumDetail";
	}

	@GetMapping(value = "/admin/album/update/{id}")
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
		return "pages/adminPage/editAlbum";
	}

	// ---------------------------------
	@PostMapping("/admin/album/update")
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
		return "redirect:/admin/album/list";
	}

	@GetMapping(value = "/admin/album/add")
	public String addAlbum(Model model) {
		Album album = new Album();
		// Lọc ra các bài hát chưa có album
		List<Song> songsWithoutAlbum = songRepository.findByAlbumIsNull();
		List<Artist> artists = artistRepository.findAll();

		model.addAttribute("songs", songsWithoutAlbum); // Chỉ truyền các bài hát chưa có album
		model.addAttribute("artists", artists);
		model.addAttribute("album", album);
		return "pages/adminPage/addAlbum";
	}

	@PostMapping(value = "/admin/album/add")
	public String addAlbum(
			@ModelAttribute("album") Album album,
			@RequestParam("selectedSongs") String songIds,
			@RequestParam("imageFile") MultipartFile imageFile) throws IOException {

		// Validate image file
		if (imageFile.isEmpty()) {
			throw new RuntimeException("Please select an image file");
		}

		if (!imageFile.getContentType().startsWith("image/")) {
			throw new RuntimeException("Only image files are allowed for cover");
		}

		// Upload file and set path
		String imagePath = fileStorageService.storeFile(imageFile);
		album.setCoverImage(imagePath);

		// Parse song IDs
		List<Long> songIdList = Arrays.stream(songIds.split(","))
				.filter(id -> !id.isEmpty())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		List<Song> selectedSongs = songRepository.findAllById(songIdList);

		// Important: set album for each song!
		for (Song song : selectedSongs) {
			song.setAlbum(album); // This links the song to the album
		}

		// Save the album first (if it's new and has no ID yet)
		albumRepository.save(album);

		// Save songs after setting album
		songRepository.saveAll(selectedSongs);

		return "redirect:/admin/album/list";
	}

	@GetMapping(value = "/admin/album/delete/{id}")
	public String deleteAlbum(@PathVariable(value = "id") Long id) throws AlbumNotFoundException {
		Album album = albumRepository.findById(id)
				.orElseThrow(() -> new AlbumNotFoundException("Album not found"));

		// Ngắt kết nối các bài hát khỏi album
		for (Song song : album.getSongsOfAlbum()) {
			song.setAlbum(null); // Ngắt kết nối bài hát với album
		}

		// Sau đó, xóa album
		albumRepository.delete(album);

		return "redirect:/admin/album/list";
	}
	//
}
