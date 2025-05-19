package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lms.cdac.entities.User;
import com.lms.cdac.forms.UserForms;
import com.lms.cdac.helper.Message;
import com.lms.cdac.helper.MessageType;
import com.lms.cdac.services.UserService;
import com.lms.cdac.services.InstitutionService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/home")
public class PageController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private InstitutionService institutionService;
    
	
    @GetMapping("/test")
    public String home() {
        System.out.println("Home Page handler");
        return "home"; // Returns the view name for home page
    }

    @GetMapping("/about")
    public String aboutPage() {
        System.out.println("About Page handler");
        return "about"; // Returns the view name for about page
    }
    
    @GetMapping("/oneDayProgram")
    public String oneDay() {
        return "ai-one-day"; // Returns the view name for about page
    }

    @GetMapping("/services")
    public String servicesPage() {
        System.out.println("Services Page handler");
        return "services"; // Returns the view name for services page
    }

    @GetMapping("/contact")
    public String contactPage() {
        System.out.println("Contact Page handler");
        return "contact"; // Returns the view name for contact page
    }
    
    @GetMapping("/courses")
    public String coursesPage() {
        System.out.println("Courses Page handler");
        return "courses"; // Returns the view name for courses page
    }

    @GetMapping("/login")
    public String loginPage() {
        // Check if the user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            
            // Redirect based on role
            User user = (User) authentication.getPrincipal();
            
            // Check roles in order of priority (same as CustomAuthenticationSuccessHandler)
            if (user.hasRole("ADMIN")) {
                return "redirect:/user/dashboard";
            } else if (user.hasRole("RESOURCE_CENTER")) {
                return "redirect:/center/dashboard";
            } else if (user.hasRole("FACULTY")) {
                return "redirect:/faculty/dashboard";
            } else if (user.hasRole("PMU_NOIDA")) {
                return "redirect:/pmu/noida/dashboard";
            } else if (user.hasRole("PMU_MOHALI")) {
                return "redirect:/pmu/mohali/dashboard";
            } else if (user.hasRole("STUDENT")) {
                return "redirect:/student/dashboard";
            } else {
                // Default fallback
                return "redirect:/student/dashboard";
            }
        }
        
        System.out.println("Login Page handler");
        return "login"; // Returns the view name for login page
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        // Check if user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            
            // Redirect based on role
            User user = (User) authentication.getPrincipal();
            
            // Check roles in order of priority (same as CustomAuthenticationSuccessHandler)
            if (user.hasRole("ADMIN")) {
                return "redirect:/user/dashboard";
            } else if (user.hasRole("RESOURCE_CENTER")) {
                return "redirect:/center/dashboard";
            } else if (user.hasRole("FACULTY")) {
                return "redirect:/faculty/dashboard";
            } else if (user.hasRole("PMU_NOIDA")) {
                return "redirect:/pmu/noida/dashboard";
            } else if (user.hasRole("PMU_MOHALI")) {
                return "redirect:/pmu/mohali/dashboard";
            } else if (user.hasRole("STUDENT")) {
                return "redirect:/student/dashboard";
            } else {
                // Default fallback
                return "redirect:/student/dashboard";
            }
        }
        
        model.addAttribute("userForm", new UserForms());
        model.addAttribute("resourceCenters", institutionService.getAllInstitutions());
        return "register"; // Returns the view name for register page
    }

    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute("userForm") UserForms userForm, 
                                  BindingResult bindingResult, 
                                  Model model,
                                  HttpSession session) {
        System.out.println("Processing Registration");
        System.out.println(userForm); // Log form data
        
        // Validate form data
        if (bindingResult.hasErrors()) {
            System.out.println("Error in registration form");
            model.addAttribute("resourceCenters", institutionService.getAllInstitutions());
            return "register"; // Return to the registration page if errors exist
        }

        // Save the user to the database with college and resource center names
        User user = User.builder()
                .name(userForm.getName())
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .phoneNumber(userForm.getPhoneNumber())
                .college(userForm.getCollege()) // Store college name directly
                .resourceCenter(userForm.getResourceCenter()) // Store resource center name directly
                .enabled(true) // Enable the user by default
                .build();

        // Save the user first
        User savedUser = userService.saveUser(user);
        
        // Assign STUDENT role to the new user
        userService.assignRole(savedUser.getUserId(), "STUDENT");
        
        // Add a success message to the session
        Message message = Message.builder()
                .content("Registration Successful")
                .type(MessageType.green) // GREEN for success
                .build();
        session.setAttribute("message", message);
        
        // Redirect to the login page after registration
        return "redirect:/home/login"; 
    }
    
   
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageName", "Restricted Page"); // Dynamically add page name
        return "access-denied"; // Returns the access-denied page
    }

    @GetMapping("/course/ai-healthcare-intro")
    public String courseDetails() {
        return "course-details";
    }
}
