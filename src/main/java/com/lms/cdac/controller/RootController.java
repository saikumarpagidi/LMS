package com.lms.cdac.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.lms.cdac.entities.User;
import com.lms.cdac.helper.Helper;
import com.lms.cdac.services.UserService;

@ControllerAdvice
@Controller
public class RootController {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
		
		  if (authentication == null) {
			 return;
			  }
		  System.out.println("Adding logged in user information to the model"); 
		 
        String username = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("User logged in: {}", username);
        // database se data ko fetch : get user from db :
        User user = userService.getUserByEmail(username);
        System.out.println(user);
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        model.addAttribute("loggedInUser", user);
    }
    
    /**
     * Handles the root URL and redirects to the home page
     * @return Redirect to home page
     */
    @GetMapping("/")
    public String rootPage() {
        logger.info("Root page requested, redirecting to home page");
        return "redirect:/home/test";
    }
}

