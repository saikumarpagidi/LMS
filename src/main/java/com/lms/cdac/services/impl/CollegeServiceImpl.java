package com.lms.cdac.services.impl;

import com.lms.cdac.entities.College;
import com.lms.cdac.entities.Institution;
import com.lms.cdac.repsitories.CollegeRepository;
import com.lms.cdac.services.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;

    @Override
    public College saveCollege(College college) {
        return collegeRepository.save(college);
    }

    @Override
    public List<College> getCollegesByResourceCenter(Institution resourceCenter) {
        return collegeRepository.findByResourceCenter(resourceCenter);
    }

    @Override
    public boolean existsByCollegeNameAndResourceCenter(String collegeName, Institution resourceCenter) {
        return collegeRepository.existsByCollegeNameAndResourceCenter(collegeName, resourceCenter);
    }
} 