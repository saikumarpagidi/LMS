package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/home")
public class PageController {
	
	@Autowired
	private UserService userService;
    
	@GetMapping("/home")
	public String index() {
		return "redirect:/home/test";
	}
    
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

    @GetMapping("/login")
    public String loginPage() {
        System.out.println("Login Page handler");
        return "login"; // Returns the view name for login page
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userForm", new UserForms());
        return "register"; // Returns the view name for register page
    }

    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute("userForm") UserForms userForm, 
                                  BindingResult bindingResult, 
                                  HttpSession session) {
        System.out.println("Processing Registration");
        System.out.println(userForm); // Log form data
        
        // Validate form data
        if (bindingResult.hasErrors()) {
            System.out.println("Error in registration form");
            return "register"; // Return to the registration page if errors exist
        }

        // Save the user to the database
        User user = User.builder()
                .name(userForm.getName())
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .phoneNumber(userForm.getPhoneNumber())
                .college(userForm.getCollege())
                .resourceCenter(userForm.getResourceCenter())
                .enabled(true) // Enable the user by default
                .build();

        userService.saveUser(user);
        
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
}
