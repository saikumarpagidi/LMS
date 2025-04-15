package com.lms.cdac.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lms.cdac.entities.AssignRole;
import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.entities.User;
import com.lms.cdac.service.RoleUserService;
import com.lms.cdac.services.AssignRoleService;
import com.lms.cdac.services.UserService;

@Controller
@RequestMapping("/assign")
public class AssignRoleController {

    private final AssignRoleService assignRoleService;
    private final UserService userService;
    private final RoleUserService roleUserService;

    @Autowired
    public AssignRoleController(AssignRoleService assignRoleService, RoleUserService roleUserService, UserService userService) {
        this.assignRoleService = assignRoleService;
        this.userService = userService;
        this.roleUserService = roleUserService;
    }

    // Show list of assigned roles
    @GetMapping("/assignview")
    public String showAssignRoleView(Model model) {
        model.addAttribute("assign", assignRoleService.getAllAssignedRoles());
        return "user/assignview";
    }

    // Show the form for assigning roles
    @GetMapping("/assignrole")
    public String showAssignRoleForm(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleUserService.getAllRoles());
        return "user/assignrole";
    }

    // Handle the role assignment POST request
    @PostMapping("/role")
    public String saveAssignRole(@RequestParam("userId") String userId, 
                                 @RequestParam("roleId") Long roleId, 
                                 RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "❌ User not found.");
                return "redirect:/assign/assignrole";
            }
            User user = userOpt.get();

            Optional<RoleUser> roleOpt = roleUserService.findById(roleId);
            if (!roleOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "❌ Role not found.");
                return "redirect:/assign/assignrole";
            }

            RoleUser roleUser = roleOpt.get();

            // Get the logged-in user and check if they are allowed to assign this role
            User loggedInUser = (User) authentication.getPrincipal(); // Get logged-in user

            if ("ADMIN".equals(roleUser.getRoleName()) && !isUserAdmin(loggedInUser)) {
                redirectAttributes.addFlashAttribute("error", "⚠️ You do not have permission to assign admin role.");
                return "redirect:/assign/assignrole";
            }

            if (assignRoleService.existsByUserAndRoleUser(user, roleUser)) {
                redirectAttributes.addFlashAttribute("error", "⚠️ This role is already assigned to the user.");
                return "redirect:/assign/assignrole";
            }

            // Creating the AssignRole and setting the user and roleUser
            AssignRole assignRole = new AssignRole();
            assignRole.setUser(user);         // Set user
            assignRole.setRoleUser(roleUser); // Set roleUser (Role)

            // Save the assignRole object
            assignRoleService.saveAssignRole(assignRole);

            redirectAttributes.addFlashAttribute("message", "✅ Role assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error assigning role: " + e.getMessage());
        }
        return "redirect:/assign/assignview";
    }

    // Handle deleting assigned roles
    @GetMapping("/delete/{id}")
    public String deleteAssignedRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!assignRoleService.existsById(id)) {
                redirectAttributes.addFlashAttribute("error", "❌ Assigned role not found.");
                return "redirect:/assign/assignview";
            }

            assignRoleService.deleteAssignRole(id);
            redirectAttributes.addFlashAttribute("message", "✅ Assigned role deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error deleting role: " + e.getMessage());
        }
        return "redirect:/assign/assignview";
    }

    private boolean isUserAdmin(User user) {
        return user.getAssignedRoles().stream()
                .anyMatch(assignRole -> "ADMIN".equals(assignRole.getRoleUser().getRoleName()));
    }
}
