package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assign_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_user_id", referencedColumnName = "id", nullable = false)
    private RoleUser roleUser;

    @Column(name = "resource_center")
    private String resourceCenter;

    @PreRemove
    private void preRemove() {
        if (user != null) {
            user.getAssignedRoles().remove(this);
        }
        if (roleUser != null) {
            roleUser.getAssignedRoles().remove(this);
        }
    }
}
