package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.MoodEntry;
import com.example.mentalhealth.model.ProgressInsight;
import com.example.mentalhealth.model.User;
import com.example.mentalhealth.service.MoodEntryService;
import com.example.mentalhealth.service.ProgressInsightService;
import com.example.mentalhealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/progress")
public class ProgressController {
    @Autowired
    private MoodEntryService moodEntryService;
    @Autowired
    private ProgressInsightService progressInsightService;
    @Autowired
    private UserRepository userRepository;

    // UC016: Complete Mood Tracker (Student)
    @GetMapping("/mood")
    public String moodTracker(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null)
            return "error/404";
        List<MoodEntry> entries = moodEntryService.getEntriesForUser(user);
        model.addAttribute("entries", entries);
        return "progress/mood-tracker";
    }

    @PostMapping("/mood")
    public String submitMood(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String mood,
            @RequestParam(required = false) String note) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null)
            return "error/404";
        MoodEntry entry = new MoodEntry();
        entry.setUser(user);
        entry.setDate(LocalDate.now());
        entry.setMood(mood);
        entry.setNote(note);
        moodEntryService.saveEntry(entry);
        return "redirect:/progress/mood";
    }

    // UC017: Monitor Student Progress (Counsellor)
    @GetMapping("/students/{id}")
    public String monitorStudentProgress(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return "error/404";
        List<MoodEntry> entries = moodEntryService.getEntriesForUser(user);
        model.addAttribute("student", user);
        model.addAttribute("entries", entries);
        return "progress/student-progress";
    }

    // UC018: View Progress Insights Dashboard
    @GetMapping("/dashboard")
    public String progressDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null)
            return "error/404";
        List<ProgressInsight> insights = progressInsightService.getInsightsForUser(user);
        model.addAttribute("insights", insights);
        return "progress/dashboard";
    }
}
