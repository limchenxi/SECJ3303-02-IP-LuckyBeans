package com.example.mentalhealth.service;

import com.example.mentalhealth.model.AwarenessCampaign;
import com.example.mentalhealth.repository.AwarenessCampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwarenessCampaignService {
    @Autowired
    private AwarenessCampaignRepository awarenessCampaignRepository;

    public List<AwarenessCampaign> getAllCampaigns() {
        return awarenessCampaignRepository.findAll();
    }

    public AwarenessCampaign saveCampaign(AwarenessCampaign campaign) {
        return awarenessCampaignRepository.save(campaign);
    }
}
