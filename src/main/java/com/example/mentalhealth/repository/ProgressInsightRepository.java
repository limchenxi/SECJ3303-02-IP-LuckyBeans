package com.example.mentalhealth.repository;

import com.example.mentalhealth.model.ProgressInsight;
import com.example.mentalhealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgressInsightRepository extends JpaRepository<ProgressInsight, Long> {
    List<ProgressInsight> findByUser(User user);
}
