package com.lms.cdac.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.lms.cdac.entities.RoleUser;
import com.lms.cdac.service.RoleUserService;

@Controller
@RequestMapping("/user/coordinator")
public class RoleController {

    private final RoleUserService roleUserService;

    @Autowired
    public RoleController(RoleUserService roleUserService) {
        this.roleUserService = roleUserService;
    }

    @GetMapping("/role")
    public String showRoles(Model model) {
        model.addAttribute("roles", roleUserService.getAllRoles());
        return "user/role";
    }

    @GetMapping("/add")
    public String viewRoles(Model model) {
        model.addAttribute("roleForm", new RoleUser());
        return "user/add_coordinator";
    }

    @PostMapping("/add")
    public String addRole(@ModelAttribute("roleForm") RoleUser roleUser, Model model) {
        // Log the incoming role name for debugging
        System.out.println("Role name received: " + roleUser.getRoleName());

        try {
            roleUserService.addRole(roleUser);  // Adds the role
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());  // Show error message if role already exists
            return "user/add_coordinator";  // Return to the same page
        }
        return "redirect:/user/coordinator/role";  // Redirect to role list
    }

    @GetMapping("/update-role/{roleId}")
    public String showUpdateRoleForm(@PathVariable Long roleId, Model model) {
        RoleUser roleUser = roleUserService.findById(roleId) // ✅ Correct
            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        model.addAttribute("roleForm", roleUser);
        return "user/update";
    }

    @PostMapping("/update")
    public String updateRole(@RequestParam Long id, @RequestParam String roleName) {
        roleUserService.updateRole(id, roleName);
        return "redirect:/user/coordinator/role";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleUserService.deleteRole(id);
        return "redirect:/user/coordinator/role";
    }
}
