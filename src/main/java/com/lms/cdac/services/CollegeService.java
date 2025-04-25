package com.lms.cdac.services;

import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;
import java.util.List;

public interface CollegeService {
    College saveCollege(College college);
    List<College> getCollegesByResourceCenter(Institution resourceCenter);
    boolean existsByCollegeNameAndResourceCenter(String collegeName, Institution resourceCenter);
}