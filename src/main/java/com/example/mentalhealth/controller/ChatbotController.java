package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.User;
import com.example.mentalhealth.model.ChatMessage;
import com.example.mentalhealth.repository.UserRepository;
import com.example.mentalhealth.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String chatbotPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("messages", chatbotService.getChatHistory(user.getId()));
        return "chatbot";
    }

    @PostMapping("/send")
    @ResponseBody
    public ChatMessage sendMessage(@RequestParam String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return chatbotService.processMessage(user.getId(), message);
    }
}
