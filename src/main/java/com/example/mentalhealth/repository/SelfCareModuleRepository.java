package com.example.mentalhealth.repository;

import com.example.mentalhealth.model.SelfCareModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelfCareModuleRepository extends JpaRepository<SelfCareModule, Integer> {
    List<SelfCareModule> findByCategory(String category);
    List<SelfCareModule> findByIsLockedOrderByModuleIdAsc(Boolean isLocked);
}
