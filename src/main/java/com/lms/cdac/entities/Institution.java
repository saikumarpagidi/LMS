package com.lms.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "institutions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "college_name") // Column for storing college/institution names
    private String collegeName;

    @Column(name = "resource_center") // Column for storing resource center names
    private String resourceCenter;
    
    @Column(name = "location") // Either "PMU Noida" or "PMU Delhi"
    private String location;  
}
