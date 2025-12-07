package com.example.mentalhealth.service;

import com.example.mentalhealth.model.User;
import com.example.mentalhealth.model.ChatMessage;
import com.example.mentalhealth.repository.UserRepository;
import com.example.mentalhealth.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatbotService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<ChatMessage> getChatHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);
    }
    
    public ChatMessage processMessage(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // Save user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUser(user);
        userMsg.setMessage(message);
        userMsg.setType(ChatMessage.MessageType.USER);
        chatMessageRepository.save(userMsg);
        
        // Generate bot response
        String botResponse = generateResponse(message);
        ChatMessage botMsg = new ChatMessage();
        botMsg.setUser(user);
        botMsg.setMessage(botResponse);
        botMsg.setType(ChatMessage.MessageType.BOT);
        
        return chatMessageRepository.save(botMsg);
    }
    
    private String generateResponse(String userMessage) {
        String msg = userMessage.toLowerCase();
        
        if (msg.contains("recommend") || msg.contains("get recommendations")) {
            return "Thank you for sharing that with me. I'm here to support you. Would you like to tell me more about how you're feeling, or would you prefer some specific recommendations for mental wellness activities?";
        } else if (msg.contains("anxious") || msg.contains("stress")) {
            return "I understand you're feeling stressed. Here are some recommendations: 1) Try breathing exercises 2) Practice mindfulness meditation 3) Consider our self-care modules. Would you like more details on any of these?";
        } else if (msg.contains("sad") || msg.contains("depressed")) {
            return "I'm sorry you're going through this. It's important to reach out for support. I recommend: 1) Speaking with a counselor 2) Engaging in mood tracking 3) Joining our peer support forum. How can I help you further?";
        } else {
            return "Thank you for sharing that with me. I'm here to support you. Would you like to tell me more about how you're feeling, or would you prefer some specific recommendations for mental wellness activities?";
        }
    }
}