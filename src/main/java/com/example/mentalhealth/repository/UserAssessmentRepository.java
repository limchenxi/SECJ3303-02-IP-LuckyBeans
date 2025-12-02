package com.example.mentalhealth.repository;

import com.example.mentalhealth.model.UserAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAssessmentRepository extends JpaRepository<UserAssessment, Integer> {
    List<UserAssessment> findByUserIdOrderByAssessmentDateDesc(Integer userId);
}
