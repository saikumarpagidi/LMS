package com.lms.cdac.repsitories;

import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeRepository extends JpaRepository<College, String> {
    List<College> findByResourceCenter(Institution resourceCenter);
    boolean existsByCollegeNameAndResourceCenter(String collegeName, Institution resourceCenter);
} 