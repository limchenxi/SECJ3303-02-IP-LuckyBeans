package com.example.mentalhealth.repository;

import com.example.mentalhealth.model.CounsellingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounsellingSessionRepository extends JpaRepository<CounsellingSession, Long> {

    List<CounsellingSession> findByStudentId(Long studentId);

    List<CounsellingSession> findByCounsellorId(Long counsellorId);
    List<CounsellingSession> findByStatus(String status);
}
