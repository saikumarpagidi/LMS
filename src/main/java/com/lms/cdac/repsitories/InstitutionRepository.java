package com.lms.cdac.repsitories;
import com.lms.cdac.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstitutionRepository extends JpaRepository<Institution, String> {
    
    // Query to find institutions by location (PMU Noida or PMU Delhi)
    List<Institution> findByLocation(String location);
}
