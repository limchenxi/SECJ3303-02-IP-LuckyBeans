package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.User;
import com.example.mentalhealth.model.Role;
import com.example.mentalhealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role) {

        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/register?error=username_exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role.toUpperCase()));

        userRepository.save(user);
        return "redirect:/login?success=registered";
    }
}
