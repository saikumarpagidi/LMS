package com.lms.cdac.services;



import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.cdac.entities.Institution;
import com.lms.cdac.repsitories.InstitutionRepository;

@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    // Fetch institutions based on location (PMU Noida or PMU Delhi)
    public List<Institution> getInstitutionsByLocation(String location) {
        return institutionRepository.findByLocation(location);
    }
    
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    // Add a new institution
    public Institution addInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    // Update an existing institution
    public Institution updateInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    // Delete an institution
    public void deleteInstitution(String id) {
        institutionRepository.deleteById(id);
    }

    // Get a specific institution by ID
    public Optional<Institution> getInstitutionById(String id) {
        return institutionRepository.findById(id);
    }

    // Get a specific institution by resource center name
    public Optional<Institution> getInstitutionByResourceCenter(String resourceCenter) {
        return institutionRepository.findByResourceCenter(resourceCenter);
    }
}
