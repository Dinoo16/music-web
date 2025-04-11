package com.example.Music_Web.exception;

public class GenreNotFoundException extends Exception {
    private String msg;

    public GenreNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
