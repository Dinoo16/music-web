package com.example.Music_Web.exception;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

	// Handle Song Not Found Exception
	@ExceptionHandler(SongNotFoundException.class)
	public String handleSongNotFoundException(SongNotFoundException ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		return "error"; // Return error.html page
	}

	// Handle Artist Not Found Exception
	@ExceptionHandler(ArtistNotFoundException.class)
	public String handleArtistNotFoundException(ArtistNotFoundException ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		return "error"; // Return error.html page
	}

	// Handle Album Not Found Exception
	@ExceptionHandler(AlbumNotFoundException.class)
	public String handleAlbumNotFoundException(AlbumNotFoundException ex, Model model) {
		model.addAttribute("errorMessage", ex.getMessage());
		return "error"; // Return error.html page
	}

	// Handle Runtime Exceptions
	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException exception, Model model) {
		model.addAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại!");
		return "error";
	}

	// Handle General Exceptions
	@ExceptionHandler(Exception.class)
	public String handleGlobalException(Exception exception, Model model) {
		model.addAttribute("errorMessage", exception.getMessage());
		return "error";
	}
}

