package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
        model.addAttribute("institutions", institutionService.getInstitutionsByLocation("PMU Noida"));
        return "user/pmu-noida-dashboard";
    }
    @PreAuthorize("hasRole('PMU')")
    // Dashboard for PMU Delhi
    @GetMapping("/pmu-delhi")
    public String getPMUDelhiDashboard(Model model) {
        model.addAttribute("institutions", institutionService.getInstitutionsByLocation("PMU Mohali"));
        return "user/pmu-delhi-dashboard";
    }

    // Add Institution (Form)
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("institution", new Institution());
        return "user/add-institution";
    }

    // Save new Institution
    @PostMapping("/add")
    public String addInstitution(@ModelAttribute Institution institution) {
        institutionService.addInstitution(institution);
        return "redirect:/pmu/pmu-noida"; // or wherever you want to redirect after adding
    }

    // Edit Institution (Form)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Institution institution = institutionService.getInstitutionById(id).orElseThrow(() -> new RuntimeException("Institution not found"));
        model.addAttribute("institution", institution);
        return "user/edit-institution";
    }

    // Save updated Institution
    @PostMapping("/edit")
    public String updateInstitution(@ModelAttribute Institution institution) {
        institutionService.updateInstitution(institution);
        return "redirect:/pmu/pmu-delhi"; // or whichever dashboard you're working on
    }

    // Delete Institution
    @GetMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable String id) {
        institutionService.deleteInstitution(id);
        return "redirect:/pmu/pmu-noida"; // or PMU Delhi if deleting from that dashboard
    }
}
