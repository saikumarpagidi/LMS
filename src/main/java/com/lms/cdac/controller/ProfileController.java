package com.lms.cdac.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;
import com.lms.cdac.entities.User;
import com.lms.cdac.services.CollegeService;
import com.lms.cdac.services.InstitutionService;
import com.lms.cdac.services.UserService;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private CollegeService collegeService;

    /**
     * पूरा प्रोफ़ाइल भरवाने वाला पेज
     */
    @GetMapping("/complete-profile")
    public String showCompleteProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        // Resource Center की लिस्ट DB से लें
        List<Institution> centers = institutionService.getAllInstitutions();
        model.addAttribute("resourceCenters", centers);

        return "complete-profile";
    }

    /**
     * फॉर्म सबमिट होने पर यूज़र अपडेट करें
     */
    @PostMapping("/complete-profile")
    public String finishProfile(
            @RequestParam String resourceCenterId,
            @RequestParam String collegeId,
            @RequestParam String phoneNumber,
            Principal principal) {

        User user = userService.findByEmail(principal.getName());

        Institution inst = institutionService
                .getInstitutionById(resourceCenterId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Resource Center"));

        College col = collegeService
                .getCollegeById(collegeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid College"));

        // ❗ Store only names (String) into user
        user.setResourceCenter(inst.getResourceCenter());
        user.setCollege(col.getCollegeName());
        user.setPhoneNumber(phoneNumber);

        userService.updateUser(user);
        return "redirect:/student/dashboard";
    }

    /**
     * AJAX: किसी Resource Center के कॉलेज लौटाए
     */
    @GetMapping(value = "/complete-profile/colleges", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<College>> loadColleges(@RequestParam String resourceCenterId) {
        Institution inst = institutionService
                .getInstitutionById(resourceCenterId)
                .orElse(null);
        if (inst == null) {
            return ResponseEntity.badRequest().build();
        }
        List<College> colleges = collegeService.getCollegesByResourceCenter(inst);
        return ResponseEntity.ok(colleges);
    }
}
