package com.lms.cdac.services;

import com.lms.cdac.entities.MOM;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MOMService {
    
    List<MOM> getAllMOMs();  // Fetch all MOMs
    
    MOM getMOMById(Integer id);  // Fetch a specific MOM
    
    MOM saveMOM(String title, String description, MultipartFile file);
    
    Resource loadMOMFile(String fileName);
}
