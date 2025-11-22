package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.CounsellingSession;
import com.example.mentalhealth.service.CounsellingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/counselling")
public class CounsellingController {

    @Autowired
    private final CounsellingService service;

    public CounsellingController(CounsellingService service) {
        this.service = service;
    }

    @GetMapping("/schedule")
    public String schedulePage(Model model) {
        // provide today's date string for any template min attributes
        model.addAttribute("todayDate", java.time.LocalDate.now().toString());
        return "counselling/schedule";
    }

    @PostMapping("/schedule")
    public String scheduleSubmit(@RequestParam String date,
                                 @RequestParam String time,
                                 @RequestParam(required = false) Long counsellorId) {
        // date expected in yyyy-MM-dd and time in HH:mm (from date + time inputs)
        java.time.LocalDate d = java.time.LocalDate.parse(date);
        java.time.LocalTime t = java.time.LocalTime.parse(time);
        java.time.LocalDateTime dt = java.time.LocalDateTime.of(d, t);

        CounsellingSession session = new CounsellingSession();
        session.setSessionDate(dt);
        session.setStatus("PENDING");
        if (counsellorId != null) {
            session.setCounsellorId(counsellorId);
        } else {
            session.setCounsellorId(1L); // fallback temporary ID
        }
        service.save(session);
        return "redirect:/counselling";
    }

    @GetMapping("/approval")
    public String listApproval(Model model) {
        model.addAttribute("sessions", service.findPending());
        return "counselling/approval";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        service.updateStatus(id, "APPROVED");
        return "redirect:/counselling/approval";
    }

    @GetMapping("/reject/{id}")
    public String reject(@PathVariable Long id) {
        service.updateStatus(id, "REJECTED");
        return "redirect:/counselling/approval";
    }

    @GetMapping("/book")
    public String showBookingForm(Model model) {
        model.addAttribute("session", new CounsellingSession());
        return "counselling/book";
    }

    @PostMapping("/book")
    public String submitBooking(@ModelAttribute CounsellingSession session) {
        session.setStudentId(1L); // TODO: change to login user
        service.createSession(session);
        return "redirect:/counselling/my-sessions";
    }

    @GetMapping("/my-sessions")
    public String mySessions(Model model) {
        model.addAttribute("sessions", service.getSessionsForStudent(1L));
        return "counselling/my-sessions";
    }
}
