package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.*;
import com.example.mentalhealth.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/self-care")
public class SelfCareController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SelfCareModuleRepository moduleRepository;
    
    @Autowired
    private UserModuleProgressRepository progressRepository;
    
    @GetMapping
    public String selfCare(@RequestParam(required = false) String category,
                          @AuthenticationPrincipal UserDetails userDetails,  // ✅ 添加这个
                          Model model) {
       
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
                            
        Integer userId = user.getUserId();
        
        model.addAttribute("user", user);
        
        UserProgress userProgress = new UserProgress("7-day self-care streak!");
        model.addAttribute("userProgress", userProgress);
        
        List<SelfCareModule> selfCareModules;
        if (category != null && !category.equals("all")) {
            selfCareModules = moduleRepository.findByCategory(category);
        } else {
            selfCareModules = moduleRepository.findAll();
        }
        
        List<SelfCareModule> modules = selfCareModules.stream().map(module -> {
            SelfCareModule dto = new SelfCareModule();

            dto.setModuleId(module.getModuleId()); 
            dto.setTitle(module.getTitle()); 
            dto.setDescription(module.getDescription());
            dto.setCategory(module.getCategory()); 
            dto.setLevel(module.getLevel());
            dto.setDuration(module.getDuration());  
            dto.setIcon(module.getIcon());  
            dto.setIsLocked(module.getIsLocked());  
            
            progressRepository.findByUserIdAndModuleId(userId, module.getModuleId())
                .ifPresentOrElse(
                    progress -> {
                        dto.setProgress(progress.getProgressPercentage());
                        dto.setCompleted("completed".equals(progress.getStatus()));
                    },
                    () -> {
                        dto.setProgress(0);
                        dto.setCompleted(false);
                    }
                );

            return dto;
        }).collect(Collectors.toList());
        
        List<Recommendation> recommendations = getPersonalizedRecommendations(userId, modules);
        model.addAttribute("recommendations", recommendations);
        
        Long completedCount = progressRepository.countCompletedModules(userId);
        Double avgProgress = progressRepository.getAverageProgress(userId);
        
        OverallProgress overallProgress = new OverallProgress();
        overallProgress.setPercentage(avgProgress != null ? avgProgress.intValue() : 0);
        overallProgress.setCompleted(completedCount != null ? completedCount.intValue() : 0);
        overallProgress.setTotal(modules.size());
        
        model.addAttribute("overallProgress", overallProgress);
        model.addAttribute("allModules", modules);
        model.addAttribute("category", category != null ? category : "all");
        
        return "self-care/self-care";
    }
    
    @GetMapping("/module/{id}")
    public String viewModule(@PathVariable Integer id,
                            @AuthenticationPrincipal UserDetails userDetails,  // ✅ 添加这个
                            Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Integer userId = user.getUserId();
        
        SelfCareModule module = moduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Module not found"));
        
        UserModuleProgress progress = progressRepository
            .findByUserIdAndModuleId(userId, id)
            .orElseGet(() -> {
                UserModuleProgress newProgress = new UserModuleProgress();
                newProgress.setUserId(userId);
                newProgress.setModuleId(id);
                newProgress.setProgressPercentage(0);
                newProgress.setStatus("in_progress");
                newProgress.setStartDate(LocalDateTime.now());
                newProgress.setLastAccessed(LocalDateTime.now());
                return progressRepository.save(newProgress);
            });
        
        progress.setLastAccessed(LocalDateTime.now());
        progressRepository.save(progress);
        
        model.addAttribute("module", module);
        model.addAttribute("progress", progress);
        
        return "module-detail";
    }
    
    @PostMapping("/module/{id}/update-progress")
    @ResponseBody
    public String updateProgress(@PathVariable Integer id,
                                 @RequestParam Integer progress,
                                 @AuthenticationPrincipal UserDetails userDetails) {  // ✅ 改这里
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            Integer userId = user.getUserId();
            
            if (progress < 0 || progress > 100) {
                return "{\"success\": false, \"message\": \"Progress must be between 0 and 100\"}";
            }
            
            UserModuleProgress moduleProgress = progressRepository
                .findByUserIdAndModuleId(userId, id)
                .orElseGet(() -> {
                    UserModuleProgress newProgress = new UserModuleProgress();
                    newProgress.setUserId(userId);
                    newProgress.setModuleId(id);
                    newProgress.setStartDate(LocalDateTime.now());
                    return newProgress;
                });
            
            moduleProgress.setProgressPercentage(progress);
            if (progress >= 100) {
                moduleProgress.setStatus("completed");
                if (moduleProgress.getCompletionDate() == null) {
                    moduleProgress.setCompletionDate(LocalDateTime.now());
                }
            } else {
                moduleProgress.setStatus("in_progress");
            }
            moduleProgress.setLastAccessed(LocalDateTime.now());
            
            progressRepository.save(moduleProgress);
            
            return "{\"success\": true, \"message\": \"Progress updated successfully\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/module/{id}/reset")
    @ResponseBody
    public String resetProgress(@PathVariable Integer id,
                               @AuthenticationPrincipal UserDetails userDetails) {  // ✅ 改这里
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            Integer userId = user.getUserId();
            
            progressRepository.findByUserIdAndModuleId(userId, id)
                .ifPresent(progress -> {
                    progress.setProgressPercentage(0);
                    progress.setStatus("in_progress");
                    progress.setCompletionDate(null);
                    progress.setLastAccessed(LocalDateTime.now());
                    progressRepository.save(progress);
                });
            
            return "{\"success\": true, \"message\": \"Progress reset successfully\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    
    private List<Recommendation> getPersonalizedRecommendations(Integer userId, List<SelfCareModule> allModules) {
        List<Recommendation> recommendations = new ArrayList<>();

        List<UserModuleProgress> inProgressList = progressRepository.findByUserIdAndStatus(userId, "in_progress");

        for (UserModuleProgress progress : inProgressList) {
            if (recommendations.size() >= 3) break;

            moduleRepository.findById(progress.getModuleId()).ifPresent(module -> {
                Recommendation rec = new Recommendation();
                rec.setIcon(module.getIcon());
                rec.setTitle(module.getTitle());
                rec.setDescription("You're " + progress.getProgressPercentage() + "% through - great time to continue!");
                rec.setBasedOn("In-progress module");
                rec.setLevel(module.getLevel());
                rec.setDuration(module.getDuration());
                rec.setProgress(progress.getProgressPercentage());
                rec.setButtonText("Continue Module");
                rec.setModuleId(module.getModuleId());
                rec.setIsAI(true);
                rec.setAiBadge("AI");
                recommendations.add(rec);
            });
        }

        if (recommendations.size() < 3) {
            List<SelfCareModule> unlockedModules = moduleRepository.findByIsLockedOrderByModuleIdAsc(false);

            for (SelfCareModule module : unlockedModules) {
                if (recommendations.size() >= 3) break;

                boolean isStarted = progressRepository
                    .findByUserIdAndModuleId(userId, module.getModuleId())
                    .isPresent();

                if (!isStarted) {
                    Recommendation rec = new Recommendation();
                    rec.setIcon(module.getIcon());
                    rec.setTitle(module.getTitle());
                    rec.setDescription("This practice could be beneficial for you");
                    rec.setBasedOn("Recommended based on your profile");
                    rec.setLevel(module.getLevel());
                    rec.setDuration(module.getDuration());
                    rec.setProgress(0);
                    rec.setButtonText("Start Module");
                    rec.setModuleId(module.getModuleId());
                    rec.setIsAI(true);
                    rec.setAiBadge("AI");
                    recommendations.add(rec);
                }
            }
        }

        return recommendations;
    }
}