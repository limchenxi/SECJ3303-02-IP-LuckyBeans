package com.example.mentalhealth.service;

import com.example.mentalhealth.model.CounsellingSession;
import com.example.mentalhealth.repository.CounsellingSessionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounsellingService {

    private final CounsellingSessionRepository repo;

    public CounsellingService(CounsellingSessionRepository repo) {
        this.repo = repo;
    }

        public CounsellingSession createSession(CounsellingSession session) {
        return repo.save(session);
    }

    public List<CounsellingSession> getSessionsForStudent(Long studentId) {
        return repo.findByStudentId(studentId);
    }

    public List<CounsellingSession> getSessionsForCounsellor(Long counsellorId) {
        return repo.findByCounsellorId(counsellorId);
    }

    public void save(CounsellingSession session) {
        repo.save(session);
    }

    public List<CounsellingSession> findPending() {
        return repo.findByStatus("PENDING");
    }

    public void updateStatus(Long id, String string) {
        repo.findById(id).ifPresent(s -> {
            s.setStatus(string);
            repo.save(s);
        });
    }
}
