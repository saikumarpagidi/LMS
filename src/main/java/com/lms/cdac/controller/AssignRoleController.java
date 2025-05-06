package com.lms.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.lms.cdac.services.AssignRoleService;
import com.lms.cdac.services.UserService;
import com.lms.cdac.service.RoleUserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/assign")
@Slf4j
public class AssignRoleController {

    private final AssignRoleService assignRoleService;
    private final UserService userService;
    private final RoleUserService roleUserService;

    @Autowired
    public AssignRoleController(AssignRoleService assignRoleService,
                                RoleUserService roleUserService,
                                UserService userService) {
        this.assignRoleService = assignRoleService;
        this.userService = userService;
        this.roleUserService = roleUserService;
    }

    /**
     * Show paginated list of assigned roles
     */
    @GetMapping("/assignview")
    public String showAssignRoleView(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        // Sort by assignment ID descending, adjust as needed
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<AssignRole> assignPage = assignRoleService.getAssignedRoles(pageable);

        model.addAttribute("assignPage", assignPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "user/assignview";
    }

    /**
     * Show assign form
     */
    @GetMapping("/assignrole")
    public String showAssignRoleForm(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleUserService.getAllRoles());
        return "user/assignrole";
    }

    /**
     * Handle role assignment
     */
    @PostMapping("/role")
    public String saveAssignRole(@RequestParam("selectedUserIds") String selectedUserIds,
                                 @RequestParam("roleId") Long roleId,
                                 @RequestParam("resourceCenter") String resourceCenter,
                                 RedirectAttributes redirectAttributes,
                                 Authentication authentication) {
        try {
            if (selectedUserIds == null || selectedUserIds.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "❌ Please select at least one user.");
                return "redirect:/assign/assignrole";
            }

            String[] userIds = selectedUserIds.split(",");
            RoleUser roleUser = roleUserService.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

            User loggedInUser = (User) authentication.getPrincipal();
            if ("ADMIN".equals(roleUser.getRoleName()) && !isUserAdmin(loggedInUser)) {
                redirectAttributes.addFlashAttribute("error", "⚠️ You do not have permission to assign admin role.");
                return "redirect:/assign/assignrole";
            }

            for (String userId : userIds) {
                userService.findById(userId).ifPresent(user -> {
                    if (!assignRoleService.existsByUserAndRoleUser(user, roleUser)) {
                        assignRoleService.assignRoleToUser(userId, roleId, resourceCenter);
                    }
                });
            }

            redirectAttributes.addFlashAttribute("message", "✅ Roles assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error: " + e.getMessage());
        }
        return "redirect:/assign/assignview";
    }

    /**
     * Delete an assigned role
     */
    @GetMapping("/delete/{id}")
    public String deleteAssignedRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("Controller: deleteAssignedRole id={}", id);
        try {
            if (!assignRoleService.existsById(id)) {
                redirectAttributes.addFlashAttribute("error", "❌ Assigned role not found.");
                return "redirect:/assign/assignview";
            }
            assignRoleService.deleteAssignRole(id);
            redirectAttributes.addFlashAttribute("message", "✅ Assigned role deleted successfully!");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error deleting role: " + e.getMessage());
        }
        return "redirect:/assign/assignview";
    }

    private boolean isUserAdmin(User user) {
        return user.getAssignedRoles().stream()
                   .anyMatch(ar -> "ADMIN".equals(ar.getRoleUser().getRoleName()));
    }
}