package com.lms.cdac.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.lms.cdac.entities.Institution;
import com.lms.cdac.services.InstitutionService;

@Controller
@RequestMapping("/pmu")
public class PmuController {

    @Autowired
    private InstitutionService institutionService;

    // PMU Noida dashboard
    @GetMapping("/pmu-noida")
    public String getPMUNoidaDashboard(Model model) {
        model.addAttribute("institutions",
            institutionService.getInstitutionsByLocation("PMU Noida"));
        return "user/pmu-noida-dashboard";
    }

    // PMU Mohali dashboard
    @GetMapping("/pmu-delhi")
    public String getPMUMohaliDashboard(Model model) {
        model.addAttribute("institutions",
            institutionService.getInstitutionsByLocation("PMU Mohali"));
        return "user/pmu-delhi-dashboard";
    }

    // Show Add form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("institution", new Institution());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> locations = new ArrayList<>();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            locations.add("PMU Noida");
            locations.add("PMU Mohali");
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PMU_NOIDA"))) {
            locations.add("PMU Noida");
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PMU_MOHALI"))) {
            locations.add("PMU Mohali");
        }

        model.addAttribute("locations", locations);
        return "user/add-institution";
    }

    @PostMapping("/add")
    public String addInstitution(@ModelAttribute Institution institution) {
        institutionService.addInstitution(institution);
        return redirectByLocation(institution.getLocation());
    }

    // Show Edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Institution inst = institutionService.getInstitutionById(id)
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        model.addAttribute("institution", inst);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> locations = new ArrayList<>();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            locations.add("PMU Noida");
            locations.add("PMU Mohali");
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PMU_NOIDA"))) {
            locations.add("PMU Noida");
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PMU_MOHALI"))) {
            locations.add("PMU Mohali");
        }

        model.addAttribute("locations", locations);
        return "user/add-institution"; // Reusing same form for edit
    }

    @PostMapping("/edit")
    public String updateInstitution(@ModelAttribute Institution institution) {
        institutionService.updateInstitution(institution);
        return redirectByLocation(institution.getLocation());
    }

    // Delete
    @GetMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable String id) {
        Institution inst = institutionService.getInstitutionById(id)
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        String loc = inst.getLocation();
        institutionService.deleteInstitution(id);
        return redirectByLocation(loc);
    }

    // Redirect based on location
    private String redirectByLocation(String loc) {
        if ("PMU Mohali".equals(loc)) {
            return "redirect:/pmu/pmu-delhi";
        } else {
            return "redirect:/pmu/pmu-noida";
        }
    }
}
