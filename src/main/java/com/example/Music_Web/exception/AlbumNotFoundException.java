package com.example.Music_Web.exception;

public class AlbumNotFoundException extends Exception {
	private String msg;

	public AlbumNotFoundException(String msg) {
		super(msg);
		this.msg = msg;
	}
}
