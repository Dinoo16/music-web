package com.example.Music_Web.controller;

import com.example.Music_Web.exception.SongNotFoundException;
import com.example.Music_Web.model.Song;
import com.example.Music_Web.model.User;
import com.example.Music_Web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị trang setting (chỉ dùng đường dẫn /setting)
    @GetMapping("/setting")
    public String showSettingPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return "redirect:/auth/signin?error=user-not-found";

        }

        model.addAttribute("user", user);
        return "pages/userPage/setting";
    }

    // Get all user list (admin mode)
    @GetMapping("/admin/userlist")
    public String showUserlistPage(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (currentUser == null) {
            return "redirect:/auth/signin?error=user-not-found";
        }

        List<User> userOnlyList = userRepository.findByRoles("USER");
        model.addAttribute("users", userOnlyList);

        return "pages/adminPage/userList";
    }

    // Get user detail by admin
    @GetMapping("/user/detail/{id}")
    public String showUserDetail(@PathVariable Long id, Principal principal, Model model) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user == null) {
            return "redirect:/auth/signin?error=user-not-found";

        }
        User getUserById = userRepository.findById(id).orElse(null);

        model.addAttribute("user", getUserById);
        return "pages/adminPage/userDetail";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/setting/changepassword")
    public String changePassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/setting"; // Nếu không tìm thấy người dùng, quay lại trang setting
        }

        // Kiểm tra xem mật khẩu mới và xác nhận mật khẩu có khớp không
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/setting"; // Nếu mật khẩu không khớp, quay lại trang setting
        }

        // Gọi phương thức changePassword của lớp User để thay đổi mật khẩu
        user.changePassword(newPassword, passwordEncoder);

        // Lưu thông tin người dùng đã cập nhật mật khẩu vào cơ sở dữ liệu
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Password changed successfully.");
        return "redirect:/setting"; // Sau khi đổi mật khẩu thành công, quay lại trang setting
    }
}
