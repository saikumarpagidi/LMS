package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lms.cdac.entities.Institution;
import com.lms.cdac.services.InstitutionService;

@Controller
@RequestMapping("/pmu")
public class PmuController {

    @Autowired
    private InstitutionService institutionService;

    // Dashboard for PMU Noida
    @GetMapping("/pmu-noida")
    public String getPMUNoidaDashboard(Model model) {
        model.addAttribute("institutions",
            institutionService.getInstitutionsByLocation("PMU Noida"));
        return "user/pmu-noida-dashboard";
    }

    // Dashboard for PMU Mohali
    @GetMapping("/pmu-delhi")
    public String getPMUMohaliDashboard(Model model) {
        model.addAttribute("institutions",
            institutionService.getInstitutionsByLocation("PMU Mohali"));
        return "user/pmu-delhi-dashboard";
    }

    // Add Institution (Form)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("institution", new Institution());
        return "user/add-institution";
    }

    // Save new Institution and redirect based on selected location
    @PostMapping("/add")
    public String addInstitution(@ModelAttribute Institution institution) {
        // 1) Save to DB
        institutionService.addInstitution(institution);

        // 2) Redirect based on location field
        String loc = institution.getLocation();
        if ("PMU Mohali".equals(loc)) {
            return "redirect:/pmu/pmu-delhi";
        } else {
            // Default to Noida
            return "redirect:/pmu/pmu-noida";
        }
    }

    // Edit Institution (Form)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Institution inst = institutionService.getInstitutionById(id)
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        model.addAttribute("institution", inst);
        return "user/edit-institution";
    }

    // Save updated Institution and redirect appropriately
    @PostMapping("/edit")
    public String updateInstitution(@ModelAttribute Institution institution) {
        institutionService.updateInstitution(institution);

        String loc = institution.getLocation();
        if ("PMU Mohali".equals(loc)) {
            return "redirect:/pmu/pmu-delhi";
        } else {
            return "redirect:/pmu/pmu-noida";
        }
    }

    // Delete Institution and redirect appropriately
    @GetMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable String id) {
        // Fetch to know location before deletion
        Institution inst = institutionService.getInstitutionById(id)
            .orElseThrow(() -> new RuntimeException("Institution not found"));
        String loc = inst.getLocation();

        institutionService.deleteInstitution(id);

        if ("PMU Mohali".equals(loc)) {
            return "redirect:/pmu/pmu-delhi";
        } else {
            return "redirect:/pmu/pmu-noida";
        }
    }
}
