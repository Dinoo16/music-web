package com.example.Music_Web.controller;

import com.example.Music_Web.dto.UserDto;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị form đăng nhập
    @GetMapping("/signin")
    public String getSignin(@RequestParam(value = "success", required = false) String success, Model model) {
        if (success != null) {
            model.addAttribute("message", "Registration successful! Please sign in.");
        }
        return "auth/signin";
    }

    // Hiển thị form đăng ký
    @GetMapping("/signup")
    public String getSignup(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/signup";
    }

    // Xử lý đăng ký
    @PostMapping("/signup")
    public String signupHandle(Model model, @Valid @ModelAttribute("user") UserDto ut, BindingResult result) {
        System.out.println("Signup method called!");

        // Kiểm tra lỗi xác thực từ annotation @Valid
        if (result.hasErrors()) {
            System.out.println("Validation failed: " + result.getAllErrors());
            return "auth/signup";
        }

        // Kiểm tra confirmPassword có khớp với password không
        if (!ut.getPassword().equals(ut.getConfirmPassword())) {
            model.addAttribute("passwordMismatch", "Passwords do not match!");
            return "auth/signup";
        }

        try {
            // Kiểm tra username đã tồn tại
            if (userRepository.findByUsername(ut.getUsername()).isPresent()) {
                model.addAttribute("error", "Username already exists!");
                return "auth/signup";
            }

            // Kiểm tra email đã tồn tại
            if (userRepository.findByEmail(ut.getEmail()).isPresent()) {
                model.addAttribute("error", "Email already exists!");
                return "auth/signup";
            }

            // Tạo user mới từ UserDto
            User newUser = new User(ut, passwordEncoder);
            userRepository.save(newUser);

            System.out.println("User saved successfully!");
            return "redirect:/auth/signin?success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error saving user: " + e.getMessage());
            return "auth/signup";
        }
    }
}
