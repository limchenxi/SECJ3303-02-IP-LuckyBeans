package com.example.mentalhealth.service;

import com.example.mentalhealth.model.EducationalResource;
import com.example.mentalhealth.repository.EducationalResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalResourceService {
    @Autowired
    private EducationalResourceRepository educationalResourceRepository;

    public List<EducationalResource> getAllResources() {
        return educationalResourceRepository.findAll();
    }

    public EducationalResource saveResource(EducationalResource resource) {
        return educationalResourceRepository.save(resource);
    }
}
