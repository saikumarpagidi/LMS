
package com.lms.cdac.entities;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails { 

    private static final long serialVersionUID = 4507709199398422657L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String userId;

    @Column(name = "user_name", nullable = true)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Getter(value = AccessLevel.NONE)
    @Column(nullable = true)
    private String password;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "college", nullable = false)
    private String college;

    @Column(name = "resource_center", nullable = false)
    private String resourceCenter;

    @Getter(value = AccessLevel.NONE)
    private boolean enabled = true;

    private boolean emailVerified = false;
    private boolean phoneVerified = false;

    @Enumerated(EnumType.STRING)
    private Providers provider = Providers.SELF;

    private String providerUserId;

    @Column(name = "email_token")
    private String emailToken;

    // 🔹 Password Reset Fields
    @Column(name = "reset_token")
    private String resetToken;
    @Column(name = "verification_token")
    private String verificationToken;
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;
    
    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    
    

    @Version
    private Long version = 0L;  // Initialize version field

    // ✅ ManyToMany Relationship for Role Management using RoleUser and AssignRole
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)  // Changed to EAGER
    private Set<AssignRole> assignedRoles = new HashSet<>();  // This handles role assignments

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return assignedRoles.stream()
                .map(assignRole -> new SimpleGrantedAuthority("ROLE_" + assignRole.getRoleUser().getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // ✅ Method to Check if User has a Specific Role
    public boolean hasRole(String roleName) {
        return assignedRoles.stream()
                .anyMatch(assignRole -> assignRole.getRoleUser().getRoleName().equalsIgnoreCase(roleName));
    }

    // ✅ Method to Assign a Role to the User
    public void addRole(RoleUser roleUser) {
        AssignRole assignRole = new AssignRole();
        assignRole.setRoleUser(roleUser);
        assignRole.setUser(this);
        this.assignedRoles.add(assignRole);
    }

    // ✅ Method to Remove a Role from the User
    public void removeRole(RoleUser roleUser) {
        assignedRoles.removeIf(assignRole -> assignRole.getRoleUser().equals(roleUser));
    }

    // ✅ Password Reset Methods
    public void generatePasswordResetToken() {
        this.resetToken = UUID.randomUUID().toString();
        this.resetTokenExpiry = LocalDateTime.now().plusMinutes(10);
    }

    public boolean isResetTokenExpired() {
        return resetTokenExpiry != null && resetTokenExpiry.isBefore(LocalDateTime.now());
    }

    public void clearPasswordResetToken() {
        this.resetToken = null;
        this.resetTokenExpiry = null;
    }
}
