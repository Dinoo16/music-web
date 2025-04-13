package com.example.Music_Web.controller;

import com.example.Music_Web.model.Album;
import com.example.Music_Web.model.Artist;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.repository.AlbumRepository;
import com.example.Music_Web.repository.ArtistRepository;
import com.example.Music_Web.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class SearchController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    // Endpoint cho tìm kiếm
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("keyword") String keyword, Model model) {
        Set<Song> allSongs = new HashSet<>();
        Set<Album> allAlbums = new HashSet<>();
        Set<Artist> allArtists = new HashSet<>();

        // Tìm kiếm nghệ sĩ theo tên
        List<Artist> artistsByName = artistRepository.findByArtistNameContainingIgnoreCase(keyword);
        allArtists.addAll(artistsByName);
        // Tìm kiếm album của các nghệ sĩ này
        for (Artist artist : artistsByName) {
            allAlbums.addAll(artist.getAlbumsOfArtist());
            for (Album album : artist.getAlbumsOfArtist()) {
                allSongs.addAll(album.getSongsOfAlbum());
            }
            allSongs.addAll(artist.getSongsOfArtist());
        }

        // Tìm kiếm album theo tên
        List<Album> albumsByName = albumRepository.findByAlbumNameContainingIgnoreCase(keyword);
        allAlbums.addAll(albumsByName);
        // Tìm kiếm bài hát trong các album này
        for (Album album : albumsByName) {
            allSongs.addAll(album.getSongsOfAlbum());
            allArtists.addAll(album.getArtistsOfAlbum());
        }

        // Tìm kiếm bài hát theo tên
        List<Song> songsByTitle = songRepository.findByTitleContainingIgnoreCase(keyword);
        allSongs.addAll(songsByTitle);
        // Tìm kiếm album và nghệ sĩ liên quan đến bài hát này (nếu chưa được thêm)
        for (Song song : songsByTitle) {
            if (song.getAlbum() != null) {
                allAlbums.add(song.getAlbum());
                allArtists.addAll(song.getAlbum().getArtistsOfAlbum());
            }
            allArtists.addAll(song.getArtistsOfSong());
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("songs", new ArrayList<>(allSongs));
        model.addAttribute("albums", new ArrayList<>(allAlbums));
        model.addAttribute("artists", new ArrayList<>(allArtists));

        return "pages/userPage/search";
    }
}