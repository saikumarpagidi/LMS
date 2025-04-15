package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mom")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MOM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // Unique ID for each MOM

    @Column(nullable = false)
    private String title;  // MOM ka title

    @Column(columnDefinition = "TEXT")
    private String description;  // MOM ka detailed description

    @Column(nullable = false)
    private String filePath;  // File ka path (PDF, DOCX, etc.)
}
