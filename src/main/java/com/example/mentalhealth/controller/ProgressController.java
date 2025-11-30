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

        // Recent mood summary (last 7 days)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<java.util.Map<String, Object>> recentMoods = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            List<MoodEntry> dayEntries = moodEntryService.getEntriesForUserByDate(user, date);
            String mood = dayEntries.isEmpty() ? "-" : dayEntries.get(0).getMood();
            java.util.Map<String, Object> entry = new java.util.HashMap<>();
            entry.put("date", date.toString());
            entry.put("mood", mood);
            recentMoods.add(entry);
        }
        model.addAttribute("recentMoods", recentMoods);

        // Mood chart data (convert mood to value)
        java.util.List<java.util.Map<String, Object>> moodChartData = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> entry : recentMoods) {
            String mood = (String) entry.get("mood");
            int moodValue = switch (mood) {
                case "Sad 😢" -> 0;
                case "Neutral 😐" -> 1;
                case "Happy 😊" -> 2;
                case "Angry 😠" -> 3;
                case "Excited 🤩" -> 4;
                default -> -1;
            };
            java.util.Map<String, Object> chartEntry = new java.util.HashMap<>();
            chartEntry.put("date", entry.get("date"));
            chartEntry.put("moodValue", moodValue);
            moodChartData.add(chartEntry);
        }
        model.addAttribute("moodChartData", moodChartData);

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

        // Mood chart for dashboard (last 7 days)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<java.util.Map<String, Object>> dashboardMoodChartData = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            List<MoodEntry> dayEntries = moodEntryService.getEntriesForUserByDate(user, date);
            String mood = dayEntries.isEmpty() ? "-" : dayEntries.get(0).getMood();
            int moodValue = switch (mood) {
                case "Sad 😢" -> 0;
                case "Neutral 😐" -> 1;
                case "Happy 😊" -> 2;
                case "Angry 😠" -> 3;
                case "Excited 🤩" -> 4;
                default -> -1;
            };
            java.util.Map<String, Object> chartEntry = new java.util.HashMap<>();
            chartEntry.put("date", date.toString());
            chartEntry.put("moodValue", moodValue);
            dashboardMoodChartData.add(chartEntry);
        }
        model.addAttribute("dashboardMoodChartData", dashboardMoodChartData);

        // Example progress percent and achievements (replace with real logic)
        model.addAttribute("progressPercent", 70);
        model.addAttribute("achievements",
                java.util.List.of("Completed 7 days tracking", "First entry", "Shared mood"));

        return "progress/dashboard";
    }
}
