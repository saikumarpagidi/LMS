package com.lms.cdac.controller;

import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;
import com.lms.cdac.services.CollegeService;
import com.lms.cdac.services.InstitutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;
    private final InstitutionService institutionService;

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddCollegeForm(Model model) {
        List<Institution> resourceCenters = institutionService.getAllInstitutions();
        model.addAttribute("resourceCenters", resourceCenters);
        model.addAttribute("college", new College());
        return "college/add-college";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCollege(@RequestParam String resourceCenterId,
                           @RequestParam String collegeName,
                           RedirectAttributes redirectAttributes) {
        Institution resourceCenter = institutionService.getInstitutionById(resourceCenterId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resource center"));

        if (collegeService.existsByCollegeNameAndResourceCenter(collegeName, resourceCenter)) {
            redirectAttributes.addFlashAttribute("error", "College already exists for this resource center");
            return "redirect:/college/add";
        }

        College college = College.builder()
                .collegeName(collegeName)
                .resourceCenter(resourceCenter)
                .build();

        collegeService.saveCollege(college);
        redirectAttributes.addFlashAttribute("success", "College added successfully");
        return "redirect:/college/add";
    }

    @RestController
    @RequestMapping("/api/college")
    public static class CollegeRestController {
        private final CollegeService collegeService;
        private final InstitutionService institutionService;

        public CollegeRestController(CollegeService collegeService, InstitutionService institutionService) {
            this.collegeService = collegeService;
            this.institutionService = institutionService;
        }

        @GetMapping(value = "/by-resource-center/{resourceCenterId}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public ResponseEntity<?> getCollegesByResourceCenter(@PathVariable String resourceCenterId) {
            log.info("Fetching colleges for resource center ID: {}", resourceCenterId);
            
            try {
                Institution resourceCenter = institutionService.getInstitutionById(resourceCenterId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid resource center ID: " + resourceCenterId));
                
                List<College> colleges = collegeService.getCollegesByResourceCenter(resourceCenter);
                log.info("Found {} colleges for resource center: {}", colleges.size(), resourceCenter.getResourceCenter());
                
                if (colleges.isEmpty()) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Collections.emptyList());
                }
                
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(colleges);
            } catch (IllegalArgumentException e) {
                log.error("Invalid resource center ID: {}", resourceCenterId);
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Invalid resource center ID"));
            } catch (Exception e) {
                log.error("Error fetching colleges: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Error fetching colleges"));
            }
        }

        @GetMapping(value = "/by-resource-center-name/{resourceCenterName}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public ResponseEntity<?> getCollegesByResourceCenterName(@PathVariable String resourceCenterName) {
            log.info("Fetching colleges for resource center: {}", resourceCenterName);
            
            try {
                Institution resourceCenter = institutionService.getInstitutionByResourceCenter(resourceCenterName)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid resource center name: " + resourceCenterName));
                
                List<College> colleges = collegeService.getCollegesByResourceCenter(resourceCenter);
                log.info("Found {} colleges for resource center: {}", colleges.size(), resourceCenterName);
                
                if (colleges.isEmpty()) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Collections.emptyList());
                }
                
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(colleges);
            } catch (IllegalArgumentException e) {
                log.error("Invalid resource center name: {}", resourceCenterName);
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Invalid resource center name"));
            } catch (Exception e) {
                log.error("Error fetching colleges: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Error fetching colleges"));
            }
        }
    }
} 