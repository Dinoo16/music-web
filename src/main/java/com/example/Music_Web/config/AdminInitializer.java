package com.example.Music_Web.config;

import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";
            String adminPassword = "Admin@123";

            // Check if admin already exists
            if (userRepo.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRoles("ADMIN");

                userRepo.save(admin);
                System.out.println(" Admin account created: username = admin, password = admin@123");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
