package com.lms.cdac.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PmuDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(PmuDashboardController.class);

    // PMU Noida Dashboard
    @GetMapping("/pmu/noida/dashboard")
    public String showNoidaDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("🔐 Logged-in User: {}", authentication.getName());
        
        model.addAttribute("welcomeMessage", "Welcome to the PMU Noida Dashboard!");
        return "PMU/pmu_noida_dashboard"; // Create this template
    }

    // PMU Mohali Dashboard
    @GetMapping("/pmu/mohali/dashboard")
    public String showMohaliDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("🔐 Logged-in User: {}", authentication.getName());
        
        model.addAttribute("welcomeMessage", "Welcome to the PMU Mohali Dashboard!");
        return "PMU/pmu_mohali_dashboard"; // Create this template
    }
} 