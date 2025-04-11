package com.example.Music_Web.exception;

public class ArtistNotFoundException extends Exception {
	private String msg;

	public ArtistNotFoundException(String msg) {
		super(msg);
		this.msg = msg;
	}
}
