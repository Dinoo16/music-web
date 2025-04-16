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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	@Autowired
	FileStorageService fileStorageService;

	@GetMapping(value = "/list")
	public String getAllAlbums(Model model) {
		List<Album> albums = albumRepository.findAll();
		// Create a map of album ID to artist names
		Map<Long, String> albumArtists = new HashMap<>();
		for (Album album : albums) {
			String artistNames = album.getArtistsOfAlbum().stream()
					.map(Artist::getArtistName)
					.collect(Collectors.joining(", "));
			albumArtists.put(album.getAlbumID(), artistNames);
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
		model.addAttribute("albumArtists", albumArtists);
		model.addAttribute("albums", albums);
		model.addAttribute("activeTab", "album");
		return "pages/adminPage/upload";
	}

	// For user page (read-only view)
	@GetMapping(value = "/user/list")
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

	@GetMapping("/detail/{id}")
	public String getAlbumDetail(@PathVariable("id") Long id, Model model) throws AlbumNotFoundException {
		Album album = albumRepository.findById(id)
				.orElseThrow(() -> new AlbumNotFoundException("Album not found"));

		String artistNames = album.getArtistsOfAlbum().stream()
				.map(Artist::getArtistName)
				.collect(Collectors.joining(", "));

		model.addAttribute("album", album);
		model.addAttribute("artistNames", artistNames);
		model.addAttribute("songs", album.getSongsOfAlbum());

		return "pages/userPage/albumDetail";
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
		// Lọc ra các bài hát chưa có album
		List<Song> songsWithoutAlbum = songRepository.findByAlbumIsNull();
		List<Artist> artists = artistRepository.findAll();

		model.addAttribute("songs", songsWithoutAlbum); // Chỉ truyền các bài hát chưa có album
		model.addAttribute("artists", artists);
		model.addAttribute("album", album);
		return "pages/adminPage/addAlbum";
	}

	@PostMapping(value = "/add")
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

		return "redirect:/album/list";
	}

	@GetMapping(value = "/delete/{id}")
	public String deleteAlbum(@PathVariable(value = "id") Long id) throws AlbumNotFoundException {
		Album album = albumRepository.findById(id)
				.orElseThrow(() -> new AlbumNotFoundException("Album not found"));

		// Ngắt kết nối các bài hát khỏi album
		for (Song song : album.getSongsOfAlbum()) {
			song.setAlbum(null); // Ngắt kết nối bài hát với album
		}

		// Sau đó, xóa album
		albumRepository.delete(album);

		return "redirect:/album/list";
	}
	//
}
