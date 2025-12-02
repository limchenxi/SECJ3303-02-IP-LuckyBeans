package com.example.mentalhealth.repository;

import com.example.mentalhealth.model.UserAssessmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAssessmentAnswerRepository extends JpaRepository<UserAssessmentAnswer, Integer> {
}
