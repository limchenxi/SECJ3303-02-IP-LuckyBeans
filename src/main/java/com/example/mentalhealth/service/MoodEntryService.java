package com.example.mentalhealth.service;

import com.example.mentalhealth.model.MoodEntry;
import com.example.mentalhealth.model.User;
import com.example.mentalhealth.repository.MoodEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MoodEntryService {
    @Autowired
    private MoodEntryRepository moodEntryRepository;

    public List<MoodEntry> getEntriesForUser(User user) {
        return moodEntryRepository.findByUser(user);
    }

    public List<MoodEntry> getEntriesForUserByDate(User user, LocalDate date) {
        return moodEntryRepository.findByUserAndDate(user, date);
    }

    public MoodEntry saveEntry(MoodEntry entry) {
        return moodEntryRepository.save(entry);
    }
}
