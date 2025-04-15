package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "role_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "roleUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AssignRole> assignedRoles;
}
