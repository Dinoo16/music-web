package com.example.Music_Web.exception;

public class PlaylistNotFoundException extends Exception {
    private String msg;

    public PlaylistNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
