package com.lms.cdac.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.User;
import com.lms.cdac.forms.UserForms;
import com.lms.cdac.services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    // Role Error
    @RequestMapping("/roleerror")
    public String roleError(Model model) {
        model.addAttribute("errorMessage", "Invalid role assigned.");
        return "user/roleerror";
    }

    // View All Users with Pagination
    @GetMapping("/users")
    public String viewUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(pageable);

        model.addAttribute("users", userPage.getContent());  
        model.addAttribute("currentPage", userPage.getNumber());  
        model.addAttribute("totalPages", userPage.getTotalPages());  
        model.addAttribute("totalItems", userPage.getTotalElements());  
        model.addAttribute("size", size);  
        return "user/userview";
    }

    // Show Only User Names in Roleview
    @GetMapping("/users/names")
    public String viewUsersForRoles(Model model) {
        model.addAttribute("users", userService.getAllUsers(PageRequest.of(0, 10)).getContent());
        return "user/roleview";
    }

    // Delete User by Email
    @PostMapping("/users/delete")
    public String deleteUserByEmail(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserByEmail(email);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/user/users";
    }

    // User Profile
    @GetMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        try {
            User user = userService.getUserByEmail(username);
            if (user == null) {
                return "user/profile";
            }
            model.addAttribute("loggedInUser", user);
        } catch (Exception e) {
            return "error/404";
        }
        return "user/profile";
    }

    // Update User Details
    @PostMapping("/update/{email}")
    public String updateUser(@PathVariable("email") String email,
                             @ModelAttribute("userForm") @Valid UserForms userForm,
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // If there are validation errors, return the form to show the errors
            return "user/updateform";  // Assuming you have an "updateform" view to return to
        }

        // Retrieve the user to update
        User user = userService.getUserByEmail(email);

        if (user != null) {
            // Update the user entity with the new data from UserForms
            user.setName(userForm.getName());
            user.setPhoneNumber(userForm.getPhoneNumber());
            user.setCollege(userForm.getCollege());  // Update college
            user.setResourceCenter(userForm.getResourceCenter());  // Update resource center

            // Save the updated user to the database
            Optional<User> updatedUser = userService.updateUser(user);

            if (updatedUser.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "User updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error updating user.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }

        return "redirect:/user/users";
    }
}
