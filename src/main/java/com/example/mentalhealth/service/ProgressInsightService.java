package com.example.mentalhealth.service;

import com.example.mentalhealth.model.ProgressInsight;
import com.example.mentalhealth.model.User;
import com.example.mentalhealth.repository.ProgressInsightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressInsightService {
    @Autowired
    private ProgressInsightRepository progressInsightRepository;

    public List<ProgressInsight> getInsightsForUser(User user) {
        return progressInsightRepository.findByUser(user);
    }

    public ProgressInsight saveInsight(ProgressInsight insight) {
        return progressInsightRepository.save(insight);
    }
}
