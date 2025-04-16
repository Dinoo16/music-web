package com.example.Music_Web.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//1.	Create Artist
//2.	Get All Artists
//4.	Update Artist
//5.	Delete Artist
@Controller
@RequestMapping(value = "/artist")
public class ArtistController {
	@Autowired
	SongRepository songRepository;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	AlbumRepository albumRepository;

	@GetMapping(value = "/list")
	public String getAllArtists(Model model) {

		// Dữ liệu cho tab song
		List<Song> songs = songRepository.findAll();
		model.addAttribute("songs", songs);
		// Dữ liệu cho tab genre
		List<Genre> genres = genreRepository.findAll();
		model.addAttribute("genres", genres);

		// Dữ liệu cho tab album
		List<Album> albums = albumRepository.findAll();
		model.addAttribute("albums", albums);

		// Dữ liệu của tab artist
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);
		// Sum of plays and songs for each artist

		model.addAttribute("activeTab", "artist");
		return "pages/adminPage/upload";

	}

	// For user page (read-only view)
	@GetMapping(value = "/user/list")
	public String getAllArtistsUser(Model model) {
		List<Artist> artists = artistRepository.findAll();
		model.addAttribute("artists", artists);

		return "pages/userPage/artist";
	}

	// Artist detail for user page (read-only view)
	@GetMapping("/detail/{id}")
	public String getArtistDetail(@PathVariable Long id, Model model) throws ArtistNotFoundException {
		Artist artist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Invalid genre ID: " + id));
		model.addAttribute("artist", artist);

		// Get all songs by this artist
		List<Song> songsOfArtist = songRepository.findByArtistsOfSong_ArtistID(id);
		// Sum of song plays
		int totalPlays = 0;
		for (Song song : songsOfArtist) {
			totalPlays += song.getPlays();
		}
		model.addAttribute("totalPlays", totalPlays);
		model.addAttribute("songsOfArtist", songsOfArtist);
		return "pages/userPage/artistDetail";
	}

	@GetMapping(value = "/update/{id}")
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

	@PostMapping("/update/{id}")
	public String updateArtist(@PathVariable Long id, @ModelAttribute Artist updatedArtist)
			throws ArtistNotFoundException {
		Artist existingArtist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Artist not found"));

		existingArtist.setArtistName(updatedArtist.getArtistName());
		existingArtist.setImage(updatedArtist.getImage());
		existingArtist.setSongsOfArtist(updatedArtist.getSongsOfArtist());
		existingArtist.setAlbumsOfArtist(updatedArtist.getAlbumsOfArtist());

		artistRepository.save(existingArtist);
		return "redirect:/artist/list";
	}

	@GetMapping(value = "/add")
	public String addArtist(Model model) {
		Artist artist = new Artist();
		List<Song> songs = songRepository.findAll();
		List<Album> albums = albumRepository.findAll();

		model.addAttribute("songs", songs);
		model.addAttribute("albums", albums);
		model.addAttribute("artist", artist);
		return "pages/adminPage/addArtist";
	}

	@PostMapping(value = "/add")
	public String addArtist(Artist artist, RedirectAttributes redirectAttributes) {
		artistRepository.save(artist);
		redirectAttributes.addFlashAttribute("activeTab", "artist"); // sử dụng flash attribute nếu redirect
		return "redirect:/artist/list";
	}

	@GetMapping(value = "/delete/{id}")
	public String deleteArtist(@PathVariable(value = "id") Long id) throws ArtistNotFoundException {
		Artist artist = artistRepository.findById(id)
				.orElseThrow(() -> new ArtistNotFoundException("Artist not found"));

		artistRepository.delete(artist);
		return "redirect:/artist/list";
	}
}
