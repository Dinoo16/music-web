package com.example.Music_Web.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class UserDto {

    @NotBlank(message = "Username is required")
    @Length(min = 6, max = 60, message = "User name length is from 6 to 60 chars")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z]).{6,60}$",
            message = "Password must be at least 6 characters long, contain at least 1 digit and 1 uppercase letter")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^\\S+@\\S+\\.\\S+$", message = "Invalid email address")
    private String email;

    private String roles;
    private String address;

    public UserDto() {}

    public UserDto(String username, String password, String confirmPassword, String email, String roles) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
