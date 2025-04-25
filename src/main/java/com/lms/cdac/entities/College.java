package com.lms.cdac.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colleges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class College {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "college_name", nullable = false)
    private String collegeName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_center_id", nullable = false)
    @JsonIgnore
    private Institution resourceCenter;

    @JsonProperty("resourceCenterId")
    public String getResourceCenterId() {
        return resourceCenter != null ? resourceCenter.getId() : null;
    }

    @JsonProperty("resourceCenterName")
    public String getResourceCenterName() {
        return resourceCenter != null ? resourceCenter.getResourceCenter() : null;
    }
} 