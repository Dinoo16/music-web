package com.example.Music_Web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {

        return "pages/home";
    }

    @GetMapping("/signin")
    public String signin() {
        return "pages/signin";
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "pages/signup";
    }

    @GetMapping("/song")
    public String getSong() {
        return "pages/song";
    }

}
