package com.lms.cdac.repsitories;
import com.lms.cdac.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, String> {
    
    // Query to find institutions by location (PMU Noida or PMU Delhi)
    List<Institution> findByLocation(String location);
    
    // Query to find an institution by resource center name
    Optional<Institution> findByResourceCenter(String resourceCenter);
    
    @Query("SELECT DISTINCT i.location FROM Institution i")
    List<String> findDistinctLocations();
    
    List<Institution> findByLocationIn(List<String> locations);

}
