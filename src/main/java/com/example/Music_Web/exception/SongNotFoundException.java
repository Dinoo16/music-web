package com.example.Music_Web.exception;

public class SongNotFoundException extends Exception {
	private String msg;

	public SongNotFoundException(String msg) {
		super(msg);
		this.msg = msg;
	}
}
