package com.example.mentalhealth.controller;

import com.example.mentalhealth.model.EducationalResource;
import com.example.mentalhealth.model.AwarenessCampaign;
import com.example.mentalhealth.service.EducationalResourceService;
import com.example.mentalhealth.service.AwarenessCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/awareness")
public class AwarenessController {
    @Autowired
    private EducationalResourceService educationalResourceService;
    @Autowired
    private AwarenessCampaignService awarenessCampaignService;

    // UC019: Access Educational Resources
    @GetMapping("/resources")
    public String listResources(Model model) {
        List<EducationalResource> resources = educationalResourceService.getAllResources();
        model.addAttribute("resources", resources);
        return "awareness/resources";
    }

    // UC020: Participate in Awareness Campaigns
    @GetMapping("/campaigns")
    public String listCampaigns(Model model) {
        List<AwarenessCampaign> campaigns = awarenessCampaignService.getAllCampaigns();
        model.addAttribute("campaigns", campaigns);
        return "awareness/campaigns";
    }
}
