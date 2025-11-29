package com.example.mentalhealth.repository;

import com.example.mentalhealth.model.MoodEntry;
import com.example.mentalhealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUser(User user);

    List<MoodEntry> findByUserAndDate(User user, LocalDate date);
}
