package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.User;
import com.example.mentalhealth.model.Role;
import com.example.mentalhealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Your custom login page
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";  // Your custom register page
    }

    
    @PostMapping("/register")
    public String register(
        @RequestParam String fullName,
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String role) {

    User user = new User();
    user.setFullName(fullName);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(Role.valueOf(role.toUpperCase()));

    userRepository.save(user);

    return "redirect:/login";
    }
}