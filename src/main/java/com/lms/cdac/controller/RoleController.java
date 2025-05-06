// src/main/java/com/lms/cdac/controller/RoleController.java
package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /** List all roles */
    @GetMapping("/role")
    public String showRoles(Model model) {
        model.addAttribute("roles", roleUserService.getAllRoles());
        return "user/role";
    }

    /** Show “add new” form */
    @GetMapping("/add")
    public String viewAddForm(Model model) {
        model.addAttribute("roleForm", new RoleUser());
        return "user/add_coordinator";
    }

    /** Handle “add new” submission */
    @PostMapping("/add")
    public String addRole(@ModelAttribute("roleForm") RoleUser roleUser,
                          RedirectAttributes ra) {
        try {
            roleUserService.addRole(roleUser);
            ra.addFlashAttribute("message", "✅ Role added successfully.");
            return "redirect:/user/coordinator/role";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/coordinator/add";
        }
    }

    /** Show the update form, mapped to update.html */
    @GetMapping("/update/{roleId}")
    public String showUpdateRoleForm(@PathVariable Long roleId, Model model) {
        RoleUser role = roleUserService.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        model.addAttribute("roleForm", role);
        return "user/update";
    }

    /** Process the update submission */
    @PostMapping("/update/{roleId}")
    public String updateRole(@PathVariable Long roleId,
                             @ModelAttribute("roleForm") RoleUser form,
                             RedirectAttributes ra) {
        try {
            roleUserService.updateRole(roleId, form.getRoleName());
            ra.addFlashAttribute("message", "✅ Role updated successfully.");
            return "redirect:/user/coordinator/role";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/coordinator/update/" + roleId;
        }
    }

    /** Delete a role */
    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id, RedirectAttributes ra) {
        roleUserService.deleteRole(id);
        ra.addFlashAttribute("message", "✅ Role deleted.");
        return "redirect:/user/coordinator/role";
    }
}
